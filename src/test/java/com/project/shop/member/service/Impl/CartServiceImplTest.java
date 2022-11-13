package com.project.shop.member.service.Impl;

import com.project.shop.factory.CartFactory;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CartCreateRequest;
import com.project.shop.member.domain.response.CartResponse;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @InjectMocks
    CartServiceImpl cartService;

    @Mock
    CartRepository cartRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    GoodsRepository goodsRepository;

    @Test
    @DisplayName("장바구니 상품 담기")
    void cartAddGoodsTest() {
        //given
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        CartCreateRequest cartCreateRequest = CartFactory.cartCreateRequest(goods, 1);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));

        //when
        cartService.cartAddGoods(cartCreateRequest, member.getId());

        //then
        verify(memberRepository).findById(any());
        verify(goodsRepository).findById(any());
        verify(cartRepository).save(any());

    }

    @Test
    @DisplayName("장바구니 회원별 조회")
    @Disabled
        // todo
    void cartFindTest() {
        //given
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Cart cart = CartFactory.cartCreate(member, goods);
        given(cartRepository.findById(member.getId())).willReturn(Optional.of(cart));

        //when
        List<CartResponse> result = cartService.cartFindMember(member.getId());

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @Disabled // todo
    @DisplayName("장바구니 상품 삭제")
    void cartDeleteGoodsTesT() {
        //given
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Cart cart = CartFactory.cartCreate(member, goods);
        goodsRepository.save(goods);
        given(goodsRepository.save(goods)).willReturn(goods);
        given(cartRepository.findById(member.getId())).willReturn(Optional.of(cart));

        //when
        cartService.cartDeleteGoods(cart.getId(), goods.getId());

        //then
        verify(cartRepository).deleteById(cart.getId());


    }
}