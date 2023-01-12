package com.project.shop.member.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.shop.global.config.security.JwtTokenDto;
import com.project.shop.global.config.security.TokenProvider;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.goods.service.Impl.S3Service;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.domain.*;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberLoginHistoryRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static com.project.shop.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.project.shop.global.error.ErrorCode.OTHER_LOGIN_TYPE;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RoleRepository roleRepository;
    private final GoodsRepository goodsRepository;
    private final ImageRepository imageRepository;
    private final CartRepository cartRepository;
    private final MemberLoginHistoryRepository memberLoginHistoryRepository;

    // 회원생성
    @Override
    public void memberSignup(MemberSignupRequest memberSignupRequest) {
        // 로그인 ID 가 중복이면 예외처리
        if (memberRepository.findByLoginId(memberSignupRequest.getLoginId()).isPresent())
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);

        Member member = Member.create(memberSignupRequest, passwordEncoder);
        memberRepository.save(member);

        for (RoleType role : memberSignupRequest.getRoles()) {
            Role saveRole = Role.builder()
                    .roleType(role)
                    .member(member)
                    .build();

            roleRepository.save(saveRole);
        }
    }

    // 회원가입 중복체크
    @Override
    public void loginIdDuplicateCheck(String loginId) {
        // 로그인 ID 가 중복이면 예외처리
        if (memberRepository.findByLoginId(loginId).isPresent())
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
    }

    // 로그인
    @Override              // String memberId, String password
    public JwtTokenDto login(LoginRequest loginRequest) throws JsonProcessingException {

        // LoginType 이 KAKAO 일때
        if (loginRequest.getLoginType().equals(LoginType.KAKAO)) {
            Member member = Member.kakaoCreate(loginRequest, passwordEncoder);

            // 동일한 Email , LoginID 일 경우 토큰만 발급 후 리턴
            if (memberRepository.findByEmailAndLoginId(loginRequest.getEmail(), loginRequest.getLoginId()).isPresent()) {
                JwtTokenDto tokenDto = tokenProvider.generateToken(loginRequest);
                return tokenDto;
            }

            // 중복 LoginID (닉네임) + 다른 이메일(다른사용자) 이면 예외처리
            if (memberRepository.findByLoginId(loginRequest.getLoginId()).isPresent())
                throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);

            Role role = Role.builder()
                    .member(member)
                    .roleType(RoleType.ROLE_USER)
                    .build();
            roleRepository.save(role);

            // member role 세팅
            member.setRoles(role);
            // 동일한 Email , 다른 LoginID 일 경우 토큰발급 + Member DB 추가 저장
            memberRepository.save(member);

            JwtTokenDto tokenDto = tokenProvider.generateToken(loginRequest);
            return tokenDto;
        }

        // 로그인한 회원의 타입이 NO_SOCIAL 이 아니라면 예외 (로그인타입 KAKAO 는 조건문으로 확인완료)
        Member member = memberRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));
        if (!member.getLoginType().equals(LoginType.NO_SOCIAL)) throw new BusinessException(OTHER_LOGIN_TYPE);

        JwtTokenDto tokenDto = tokenProvider.generateToken(loginRequest);
        return tokenDto;
    }

    // 내 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponse findByDetailMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));

        MemberResponse memberResponse = new MemberResponse().toResponse(member);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> list = authorities.stream().map(grantedAuthority -> grantedAuthority.getAuthority()).toList();
        memberResponse.setRoles(list);

        return memberResponse;

    }

    // 회원 수정
    @Override
    public void memberEdit(MemberEditRequest memberEditRequest) {
        Member member = getMember();
        member.edit(memberEditRequest, passwordEncoder);
    }

    // 회원 탈퇴
    @Override
    public void memberDelete() {
        Member member = getMember();
        // 회원 DB deleteAt 현재시간으로 초기화
        member.setDeletedAt();

        // member 권한이 USER 권한이면 장바구니 상품 삭제
        // 권한이 USER, SELLER 2개일 수도 있다.
        for (Role role : member.getRoles()) {
            if (role.getRoleType().equals(RoleType.ROLE_USER)) {
                List<Cart> cartList = cartRepository.findByMemberId(member.getId());
                cartRepository.deleteAll(cartList);
            }
            // 회원(SELLER) 이 등록한 상품이 있으면 상품에 관련된 데이터를 모두 삭제 (상품, 리뷰, 대댓글, 옵션, 상품이미지, S3)
            if (role.getRoleType().equals(RoleType.ROLE_SELLER)) {
                List<Goods> goodsList = goodsRepository.findAllByMemberId(member.getId());
                for (Goods goods : goodsList) {
                    List<Image> imageList = imageRepository.findByGoodsId(goods.getId());
                    for (Image image : imageList) {
                        String fileName = image.getFileUrl().substring(bucket.length() + 41);
                        s3Service.deleteFile(fileName);
                    }
                    goodsRepository.deleteById(goods.getId());
                }
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 00 * * *")
    public void schedulerLoginHistoryDeleteCron() {
        memberLoginHistoryRepository.deleteAll();
        System.out.println("히스토리 삭제!");
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));
        return member;
    }
}
