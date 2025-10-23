package com.project.shop.member.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.shop.global.config.security.JwtTokenDto;
import com.project.shop.global.config.security.TokenProvider;
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

import static com.project.shop.global.error.ErrorCode.*;


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
        DuplicatedLoginIdCheck(memberRepository.findByLoginId(memberSignupRequest.getLoginId()).isPresent());

        Member member = Member.create(memberSignupRequest, passwordEncoder);
        memberRepository.save(member);

        Role saveRole = Role.builder()
                .roleType(memberSignupRequest.getRoleType())
                .member(member)
                .build();

        roleRepository.save(saveRole);
    }

    // 회원가입 중복체크
    @Override
    public void loginIdDuplicateCheck(String loginId) {
        DuplicatedLoginIdCheck(memberRepository.findByLoginId(loginId).isPresent());
    }

    // 로그인
    @Override              // String memberId, String password
    public JwtTokenDto login(LoginRequest loginRequest) throws JsonProcessingException {

        // LoginType 이 KAKAO 일때
        if (loginRequest.getLoginType().equals(LoginType.KAKAO)) {
            Member member = Member.kakaoCreate(loginRequest, passwordEncoder);

            // 동일한 Email , LoginID 일 경우 토큰만 발급 후 리턴
            if (memberRepository.findByEmailAndLoginId(loginRequest.getEmail(), loginRequest.getLoginId()).isPresent()) {
                return tokenProvider.generateToken(loginRequest);
            }

            DuplicatedLoginIdCheck(memberRepository.findByLoginId(loginRequest.getLoginId()).isPresent());

            Role role = Role.builder()
                    .member(member)
                    .roleType(RoleType.ROLE_USER)
                    .build();
            roleRepository.save(role);

            // member role 세팅
            member.setRoles(role);
            // 동일한 Email , 다른 LoginID 일 경우 토큰발급 + Member DB 추가 저장
            memberRepository.save(member);

            return tokenProvider.generateToken(loginRequest);
        }

        // 로그인한 회원의 타입이 NO_SOCIAL 이 아니라면 예외 (로그인타입 KAKAO 는 조건문으로 확인완료)
        Member member = memberRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // 비밀번호 일치 여부 비교 ( 로그인 요청한 PW == DB 암호화 비밀번호 )
        if(!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new BusinessException(NOT_EQUAL_PASSWORD);
        }
        return tokenProvider.generateToken(loginRequest);
    }

    // 내 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponse findByDetailMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));

        MemberResponse memberResponse = MemberResponse.toResponse(member);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> list = authorities.stream().map(GrantedAuthority::getAuthority).toList();
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
                        String fileName = image.getFileUrl();
                        s3Service.deleteFile(fileName);
                    }
                    goodsRepository.deleteById(goods.getId());
                }
            }
        }
    }

    //로그인 히스토리 테이블 매일 자정 자동 삭제
    @Override
    @Scheduled(cron = "0 0 00 * * *")
    public void schedulerLoginHistoryDeleteCron() {
        memberLoginHistoryRepository.deleteAll();
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        return memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));
    }

    // 로그인 아이디 중복 체크
    private void DuplicatedLoginIdCheck(boolean duplicatedCheck) {
        if (duplicatedCheck) throw new BusinessException(DUPLICATED_LOGIN_ID);
    }
}
