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
import com.project.shop.global.config.security.JwtTokenDto;
import com.project.shop.global.config.security.TokenProvider;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberLoginHistoryRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RoleRepository roleRepository;
    private final GoodsRepository goodsRepository;
    private final ImageRepository imageRepository;
    private final CartRepository cartRepository;
    private final MemberLoginHistoryRepository memberLoginHistoryRepository;

    // ????????????
    @Override
    public void memberSignup(MemberSignupRequest memberSignupRequest) {
        // ????????? ID ??? ???????????? ????????????
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

    // ???????????? ????????????
    @Override
    public void loginIdDuplicateCheck(String loginId) {
        // ????????? ID ??? ???????????? ????????????
        if (memberRepository.findByLoginId(loginId).isPresent())
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
    }

    // ?????????
    @Override              // String memberId, String password
    public JwtTokenDto login(LoginRequest loginRequest) {

        // LoginType ??? KAKAO ??????
        if (loginRequest.getLoginType().equals(LoginType.KAKAO)) {
            Member member = Member.kakaoCreate(loginRequest, passwordEncoder);

            // ????????? Email , LoginID ??? ?????? ????????? ?????? ??? ??????
            if (memberRepository.findByEmailAndLoginId(loginRequest.getEmail(), loginRequest.getLoginId()).isPresent()) {
                JwtTokenDto tokenDto = tokenProvider.generateTokenNoSecurity(loginRequest);
                return tokenDto;
            }

            // ?????? LoginID (?????????) + ?????? ?????????(???????????????) ?????? ????????????
            if (memberRepository.findByLoginId(loginRequest.getLoginId()).isPresent())
                throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);

            // ????????? Email , ?????? LoginID ??? ?????? ???????????? + Member DB ?????? ??????
            memberRepository.save(member);

            Role role = Role.builder()
                    .member(member)
                    .roleType(RoleType.ROLE_USER)
                    .build();
            roleRepository.save(role);

            JwtTokenDto tokenDto = tokenProvider.generateTokenNoSecurity(loginRequest);
            return tokenDto;
        }

        // ???????????? ????????? ????????? NO_SOCIAL ??? ???????????? ?????? (??????????????? KAKAO ??? ??????????????? ????????????)
        Member member = memberRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));
        if (!member.getLoginType().equals(LoginType.NO_SOCIAL)) throw new BusinessException(OTHER_LOGIN_TYPE);

        // ?????? ?????? ?????????
        // 1. ????????? Id / Pw ??? ???????????? AuthenticationToken ??????
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());
        // 2. ?????? ?????? (????????? ???????????? ??????)
        // authenticate ???????????? ?????? ?????? loadUserByUsername ????????? ??????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 3. ?????????????????? jwt ????????????
        JwtTokenDto tokenDto = tokenProvider.generateToken(authentication);
        // 4. ?????? ??????
        return tokenDto;
    }

    // ??? ?????? ??????
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

    // ?????? ??????
    @Override
    public void memberEdit(MemberEditRequest memberEditRequest) {
        Member member = getMember();
        member.edit(memberEditRequest, passwordEncoder);
    }

    // ?????? ??????
    @Override
    public void memberDelete() {
        Member member = getMember();
        // ?????? DB deleteAt ?????????????????? ?????????
        member.setDeletedAt();

        // member ????????? USER ???????????? ???????????? ?????? ??????
        // ????????? USER, SELLER 2?????? ?????? ??????.
        for (Role role : member.getRoles()) {
            if (role.getRoleType().equals(RoleType.ROLE_USER)) {
                List<Cart> cartList = cartRepository.findByMemberId(member.getId());
                cartRepository.deleteAll(cartList);
            }
            // ??????(SELLER) ??? ????????? ????????? ????????? ????????? ????????? ???????????? ?????? ?????? (??????, ??????, ?????????, ??????, ???????????????, S3)
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
        System.out.println("???????????? ??????!");
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));
        return member;
    }
}
