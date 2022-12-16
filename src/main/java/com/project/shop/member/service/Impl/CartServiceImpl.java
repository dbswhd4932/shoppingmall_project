package com.project.shop.member.service.Impl;

import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.member.controller.request.CartEditRequest;
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
    private final OptionRepository optionRepository;

    // 장바구니 담기
    @Override
    public void cartAddGoods(CartCreateRequest cartCreateRequest, Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // CMS 시스템이 없으므로, 예외발생 할 수 있어 체크로직 추가
        Goods goods = goodsRepository.findById(cartCreateRequest.getGoodsId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_GOODS));

        Option option = optionRepository.findById(cartCreateRequest.getOptionNumber()).orElseThrow(
                () -> new IllegalArgumentException("존재하는 옵션이 없습니다."));

        // 장바구니에 존재하는 상품이면 "장바구니에 이미 담겨있는 상품입니다." 예외 발생
        if (cartRepository.findByGoodsId(goods.getId()).isPresent())
            throw new BusinessException(CART_IN_GOODS_DUPLICATED);

        int goodsTotalPrice = goods.getPrice();
        // 옵션이 있는 상품이면 상품 최종 가격변경(기본상품 + 옵션가격)
        if (!goods.getOptions().isEmpty()) {
            goodsTotalPrice = option.getTotalPrice();;
        }

        Cart cart = Cart.builder()
                .member(member)
                .goodsId(goods.getId())
                .totalAmount(cartCreateRequest.getAmount())
                .totalPrice(goodsTotalPrice * cartCreateRequest.getAmount())
                .optionNumber(cartCreateRequest.getOptionNumber())
                .build();

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

    /**
     * 상품 변경 여부를 확인할 수 있습니다.
     * 장바구니에서 상품 주문전에 해당 API 로 상품 변경 여부를 확인 후,
     * 변경된 내역(이름, 가격 등)이 있을 경우 변경된 내용으로 적용되어 주문창으로 넘어갑니다.
     * false = 변경 없음 / true = 변경 있음
     */
    @Override
    public boolean checkGoodsInfoChange(Long cartId) {

        boolean result = false;
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new BusinessException(CART_NO_PRODUCTS));

        Option option = optionRepository.findByIdAndGoodsId(cart.getOptionNumber(), cart.getGoodsId()).orElseThrow(
                () -> new IllegalArgumentException("없는 옵션번호 입니다."));

        /**
         *  "기존 장바구니 전체 금액" 과 "현재 상품 옵션 금액 * 장바구니 수량" 이 다를 경우 금액이 수정된 것
         *  EX ) A 상품 1번 옵션 2개 = 24000  가격변경 후 / A 상품 1번 옵션 2개 = 28000
         */
        if (cart.getTotalPrice() != (option.getTotalPrice() * cart.getTotalAmount())) {
            result = true;
        }
        return result;
    }

    // 장바구니 수량, 옵션 변경
    @Override
    public void editCartItem(Long cartId, CartEditRequest cartEditRequest) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_CART));

        Goods goods = goodsRepository.findById(cart.getGoodsId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_GOODS));

        cart.edit(goods, cartEditRequest);
    }

    // 장바구니 상품 삭제
    @Override
    public void cartDeleteGoods(Long cartId, Long goodsId, Long memberId) {
        Cart cart = cartRepository.findByIdAndGoodsIdAndMemberId(cartId, goodsId, memberId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cartRepository.deleteById(cart.getId());
    }
}
