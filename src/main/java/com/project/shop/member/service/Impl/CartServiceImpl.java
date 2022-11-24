package com.project.shop.member.service.Impl;

import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.project.shop.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final GoodsRepository goodsRepository;

    // 장바구니 담기
    @Override
    public void cartAddGoods(CartCreateRequest cartCreateRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));

        Goods goods = goodsRepository.findById(cartCreateRequest.getGoodsId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_GOODS));

        // 장바구니에 존재하는 상품이면 "장바구니에 존재하는 상품입니다" 예외 발생
        if (cartRepository.findByGoodsId(goods.getId()).isPresent())
            throw new BusinessException(DUPLICATE_GOODS);

        Cart cart = Cart.createCart(member, goods, cartCreateRequest);
        cartRepository.save(cart);
    }

    // 장바구니 조회
    @Override
    public List<CartResponse> cartFindMember(Long memberId) {
        List<Cart> carts = cartRepository.findByMemberId(memberId).orElseThrow(
                () -> {
                    throw new BusinessException(NOT_FOUND_CART);
                });

        List<CartResponse> list = new ArrayList<>();
        for (Cart cart : carts) {
            list.add(CartResponse.toCartResponse(cart));
        }
        return list;
    }

    // 장바구니 상품 삭제
    @Override
    public void cartDeleteGoods(Long cartId, Long goodsId, Long memberId) {
        Cart cart = cartRepository.findByIdAndGoodsIdAndMemberId(cartId, goodsId, memberId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cartRepository.deleteById(cart.getId());
    }
}
