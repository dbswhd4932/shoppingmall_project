package com.project.shop.member.service.Impl;

import com.project.shop.factory.CartFactory;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Options;
import com.project.shop.goods.domain.OptionCreate;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartPageResponse;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("장바구니 서비스 테스트")
class CartServiceImplTest {

    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    CartRepository cartRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    OptionRepository optionRepository;

    @BeforeEach()
    void beforeEach() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken mockAuthentication = new TestingAuthenticationToken("loginId", "1234");
        context.setAuthentication(mockAuthentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("장바구니 상품 담기")
    void cartAddGoodsTest() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        CartCreateRequest cartCreateRequest = CartFactory.cartCreateRequest(goods, 1);

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));
        assertThatCode(() -> optionRepository.findByGoodsId(goods.getId())).doesNotThrowAnyException();

        //when
        cartService.cartAddGoods(cartCreateRequest);

        //then
        verify(cartRepository).save(any());
    }

    @Test
    @DisplayName("장바구니 조회")
    void cartFindTest() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Cart cart = new Cart(1L, member, 1L, 10, 1000, 1L);
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        List<Cart> list = new ArrayList<>();
        list.add(cart);

        PageImpl<Cart> carts = new PageImpl<>(list);

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(cartRepository.findAllByMemberId(member.getId(),pageable)).willReturn(carts);

        //when
        List<CartPageResponse> cartPageRespons = cartService.cartFindMember(pageable);

        //then
        assertThat(cartPageRespons.size()).isEqualTo(1);
        assertThat(cartPageRespons.get(0).getTotalPrice()).isEqualTo(1000);
    }

//    @Test
//    @DisplayName("장바구니 변경")
//    void editCartItem() {
//        //given
//        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
//        Member member = memberFactory.createMember();
//        Goods goods = GoodsFactory.createGoods();
//        Options options = new Options(1L, goods, List.of(new OptionCreate("key", "value")), 1000, "설명");
//        Cart cart = new Cart(1L, member, 1L, 10, 1000, 1L);
//        CartEditRequest cartEditRequest = new CartEditRequest(100, 2L);
//        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
//        given(cartRepository.findByIdAndMember(cart.getId(), member)).willReturn(Optional.of(cart));
//        given(goodsRepository.findById(cart.getGoodsId())).willReturn(Optional.ofNullable(goods));
//        given(optionRepository.findByGoodsId(Objects.requireNonNull(goods).getId())).willReturn(List.of(options));
//
//        //when
//        cartService.editCartItem(cart.getId(), cartEditRequest);
//
//        //then
//        verify(memberRepository).findByLoginId(member.getLoginId());
//        verify(cartRepository).findByIdAndMember(cart.getId(),member);
//        verify(goodsRepository).findById(cart.getGoodsId());
//        verify(optionRepository).findByGoodsId(goods.getId());
//
//    }

    @Test
    @DisplayName("장바구니 삭제")
    void cartDeleteGoodsTest() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Cart cart = new Cart(1L, member, 1L, 10, 1000, 1L);
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(cartRepository.findByIdAndMember(cart.getId(),member)).willReturn(Optional.of(cart));

        //when
        cartService.cartDeleteGoods(cart.getId());

        //then
        verify(cartRepository).deleteById(cart.getId());
    }

}