package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CartCreateRequest;
import com.project.shop.member.domain.response.CartResponse;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // 장바구니에 원래 있는 상품이면 개수, 금액 추가
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        if (cartList.size() != 0) {
            for (Cart cart : cartList) {
                if (cart.getGoodsId().equals(goods.getId())) {
                    cart.addAmount(cart, goods, cartCreateRequest);
                    return;
                }
            }
        }

        Cart cart = Cart.builder()
                .member(member)
                .goodsId(cartCreateRequest.getGoodsId())
                .totalAmount(cartCreateRequest.getAmount())
                .totalPrice(goods.getPrice() * cartCreateRequest.getAmount())
                .build();

        cartRepository.save(cart);
    }

    // 장바구니 회원 별 조회
    @Override
    public List<CartResponse> cartFindMember(Long memberId) {
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        List<CartResponse> cartResponseList = new ArrayList<>();
        for (Cart cart : cartList) {
            if (cart.getMember().getId().equals(memberId)) {
                cartResponseList.add(CartResponse.toCartResponse(cart));
            }
        }
        if (cartResponseList.isEmpty()) {
            throw new BusinessException(NOT_FOUND_CART);
        } else {
            return cartResponseList;
        }
    }

    // 장바구니 상품 선택 삭제
    @Override
    public void cartDeleteGoods(Long cartId, Long goodsId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_CART));

        if ( cart.getGoodsId().equals(goodsId)) {
            cartRepository.deleteById(cart.getId());
        } else {
            throw new BusinessException(NOT_FOUND_GOODS);
        }
    }
}
