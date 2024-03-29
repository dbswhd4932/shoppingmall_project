package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Reply;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.ReplyRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("대댓글 서비스 테스트")
class ReplyServiceImplTest {

    @InjectMocks
    ReplyServiceImpl replyService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ReplyRepository replyRepository;

    @BeforeEach()
    void beforeEach() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken mockAuthentication = new TestingAuthenticationToken("loginId", "1234");
        context.setAuthentication(mockAuthentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("대댓글 생성")
    void replyCreate() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review = Review.builder().memberId(member.getId()).goods(goods).comment("comment").build();
        ReplyCreateRequest replyCreateRequest = ReplyCreateRequest.builder().replyComment("replyComment").build();

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(reviewRepository.findById(1L)).willReturn(Optional.ofNullable(review));

        //when
        replyService.replyCreate(1L, replyCreateRequest);

        //then
        verify(replyRepository).save(any());
    }

    @Test
    @DisplayName("대댓글 수정")
    void replyEdit() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review = new Review(1L, goods, "comment");
        Reply reply = new Reply(1L, review, "replyComment");
        ReplyEditRequest replyEditRequest = new ReplyEditRequest("editComment");
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(replyRepository.findByIdAndMemberId(1L,member.getId())).willReturn(Optional.of(reply));

        //when
        replyService.replyEdit(1L, replyEditRequest);

        //then
        assertThat(reply.getComment()).isEqualTo("editComment");

    }

    @Test
    @DisplayName("대댓글 삭제")
    void replyDelete() {
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review = new Review(1L, goods, "comment");
        Reply reply = new Reply(1L, review, "replyComment");
        ReplyEditRequest replyEditRequest = new ReplyEditRequest("editComment");
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(replyRepository.findByIdAndMemberId(1L, member.getId())).willReturn(Optional.of(reply));

        //when
        replyService.replyDelete(1L);

        //then
        verify(replyRepository).delete(reply);
    }
}