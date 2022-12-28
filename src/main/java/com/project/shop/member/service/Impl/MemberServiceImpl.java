package com.project.shop.member.service.Impl;

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
import com.project.shop.member.jwt.JwtTokenDto;
import com.project.shop.member.jwt.TokenProvider;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RoleRepository roleRepository;
    private final GoodsRepository goodsRepository;
    private final ImageRepository imageRepository;
    private final CartRepository cartRepository;

    // 회원생성
    @Override
    public void memberSignup(MemberSignupRequest memberSignupRequest) {
        if (memberRepository.findByLoginId(memberSignupRequest.getLoginId()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }

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
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

    // 로그인
    @Override              // String memberId, String password
    public JwtTokenDto login(LoginRequest loginRequest) {

        // LoginType 이 KAKAO 일때
        if (loginRequest.getLoginType().equals(LoginType.KAKAO)) {
            Member member = Member.kakaoCreate(loginRequest, passwordEncoder);
            // 이미 존재하는 회원인지 확인
            if (memberRepository.findByLoginId(loginRequest.getLoginId()).isPresent())
                throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);

            memberRepository.save(member);

            Role role = Role.builder()
                    .member(member)
                    .roleType(RoleType.ROLE_USER)
                    .build();
            roleRepository.save(role);

            JwtTokenDto tokenDto = tokenProvider.generateTokenNoSecurity(loginRequest);
            return tokenDto;
        }

        // 일반 회원 로그인
        // 1. 로그인 Id / Pw 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());
        // 2. 실제 검증 (사용자 비밀번호 체크)
        // authenticate 메서드가 실행 될때 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 3. 인증기반으로 jwt 토큰생성
        JwtTokenDto tokenDto = tokenProvider.generateToken(authentication);
        // 4. 토큰 리턴
        return tokenDto;
    }

    // 내 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponse findByDetailMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        MemberResponse memberResponse = new MemberResponse().toResponse(member);

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

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        return member;
    }
}
