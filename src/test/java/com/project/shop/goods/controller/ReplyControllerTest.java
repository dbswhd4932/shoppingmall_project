package com.project.shop.goods.controller;

import com.project.shop.factory.MemberFactory;
import com.project.shop.factory.OrderFactory;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Reply;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReplyRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.Impl.ReplyServiceImpl;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.Order;
import com.project.shop.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayName("????????? ???????????? ???????????????")
class ReplyControllerTest extends ControllerSetting {

    @Autowired
    ReplyServiceImpl replyService;

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        Order order = OrderFactory.order(member);
        Goods goods = Goods.builder()
                .memberId(member.getId())
                .goodsName("???????????????")
                .category(new Category("??????"))
                .price(10000)
                .description("??????")
                .build();
        goodsRepository.save(goods);
        orderRepository.save(order);
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("????????? ??????")
    void replyCreate() throws Exception {
        //given
        Member findMember = memberRepository.findByLoginId("loginId").get();
        Goods goods = Goods.builder().goodsName("?????????").memberId(findMember.getId()).price(10000).build();
        Goods saveGoods = goodsRepository.save(goods);
        Review review = Review.builder().memberId(findMember.getId()).goods(saveGoods).comment("commentTest").build();
        reviewRepository.save(review);

        ReplyCreateRequest replyCreateRequest = ReplyCreateRequest
                .builder()
                .replyComment("comment")
                .build();

        //when
        mockMvc.perform(post("/api/replies")
                        .with(user("loginId").roles("SELLER"))
                        .queryParam("reviewId", String.valueOf(review.getId()))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyCreateRequest)))
                .andExpect(status().isCreated());

        //then
        assertThat(reviewRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    @DisplayName("????????? ??????")
    void replyFind() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("???????????????").get();
        Review review = Review.builder().goods(goods).memberId(member.getId()).comment("comment").build();
        reviewRepository.save(review);
        Reply reply = Reply.builder()
                .review(review)
                .memberId(member.getId())
                .comment("comment")
                .build();
        replyRepository.save(reply);
        List<ReplyResponse> replyResponses = replyService.replyFind(review.getId());

        //when
        mockMvc.perform(get("/api/reviews/reply")
                        .queryParam("reviewId", String.valueOf(review.getId())))
                .andExpect(status().isOk());

        //then
        assertThat(replyResponses.get(0).getComment()).isEqualTo("comment");
    }

    @Test
    @DisplayName("????????? ??????")
    @WithMockUser(roles = "SELLER")
    void replyEdit() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("???????????????").get();
        Review review = Review.builder().goods(goods).memberId(member.getId()).comment("comment").build();
        reviewRepository.save(review);
        Reply reply = Reply.builder().review(review).memberId(member.getId()).comment("replyComment").build();
        replyRepository.save(reply);
        ReplyEditRequest replyEditRequest = ReplyEditRequest.builder().comment("editComment").build();

        //when
        mockMvc.perform(put("/api/replies/{replyId}", reply.getId())
                        .with(user("loginId").roles("SELLER"))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyEditRequest)))
                .andExpect(status().isOk());

        //then
        assertThat(reply.getComment()).isEqualTo("editComment");

    }

    @Test
    @DisplayName("????????? ??????")
    @WithMockUser(roles = "SELLER , ADMIN")
    void replyDelete() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("???????????????").get();
        Review review = Review.builder().goods(goods).memberId(member.getId()).comment("comment").build();
        reviewRepository.save(review);
        Reply reply = Reply.builder().review(review).memberId(member.getId()).comment("replyComment").build();
        replyRepository.save(reply);

        //when
        mockMvc.perform(delete("/api/replies/{replyId}", reply.getId())
                        .with(user("loginId").roles("SELLER")))
                .andExpect(status().isNoContent());

        //then
        assertThat(replyRepository.findAll().size()).isEqualTo(0);

    }

}