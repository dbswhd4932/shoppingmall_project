package com.project.shop.member.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.domain.OptionCreate;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ImportAutoConfiguration(WebSecurityConfig.class)
@WebAppConfiguration
@DisplayName("컨트롤러 장바구니 통합테스트")
class CartControllerTest extends ControllerSetting {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "loginId" , roles = "USER")
    @DisplayName("장바구니 상품 추가")
    void cartAddGoods() throws Exception {
        //given

        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000).optionValue(List.of(optionCreate)).optionDescription("설명").build();
        Goods goods = GoodsFactory.createGoods();
        Option option = Option.toOption(optionCreateRequest, goods);
        CartCreateRequest cartCreateRequest = CartCreateRequest.builder()
                .optionNumber(option.getId()).amount(10).goodsId(goods.getId()).build();

        //when
        mockMvc.perform(post("/api/carts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartCreateRequest)))
                .andExpect(status().isCreated());

        //then
        Assertions.assertThat(cartRepository.findAll().size()).isEqualTo(1);


    }

}