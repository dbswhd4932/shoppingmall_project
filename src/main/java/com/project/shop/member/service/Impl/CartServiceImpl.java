package com.project.shop.member.service.Impl;

import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void cartAddGoods(CartCreateRequest cartCreateRequest) {

        Member member = tokenCheckMember();

        Goods goods = goodsRepository.findById(cartCreateRequest.getGoodsId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_GOODS));

        // 옵션이 있는 상품일 경우 optionRepository 에서 찾아서 초기화 해줍니다.
        Option option = null;
        if (!optionRepository.findByGoodsId(cartCreateRequest.getGoodsId()).isEmpty()) {
            option = optionRepository.findByIdAndGoodsId(cartCreateRequest.getOptionNumber(), goods.getId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_OPTION));
        }

        // 장바구니에 존재하는 상품이면 "장바구니에 이미 담겨있는 상품입니다." 예외 발생가 발생합니다.
        if (cartRepository.findByGoodsId(goods.getId()).isPresent())
            throw new BusinessException(CART_IN_GOODS_DUPLICATED);

        int goodsTotalPrice = goods.getPrice();
        // 옵션이 있는 상품이면 상품 최종 가격변경(기본상품 + 옵션가격)
        if (!goods.getOptions().isEmpty()) {
            goodsTotalPrice = option.getTotalPrice();
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
    @Transactional(readOnly = true)
    public List<CartResponse> cartFindMember() {

        Member member = tokenCheckMember();

        List<Cart> carts = cartRepository.findByMemberId(member.getId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_CART));

        List<CartResponse> list = new ArrayList<>();
        for (Cart cart : carts) {
            list.add(CartResponse.toCartResponse(cart));
        }
        return list;
    }

    /**
     * 상품 변경 여부를 확인할 수 있습니다.
     * 장바구니에서 상품 주문전에 해당 API 로 상품 변경 여부를 확인 후,
     * 변경된 내역(가격)이 있을 경우 변경된 내용으로 적용되어 주문창으로 넘어갑니다.
     * false = 변경 없음 / true = 변경 있음
     */
    @Override
    public boolean checkGoodsInfoChange(Long cartId) {

        boolean result = false;
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new BusinessException(CART_NO_PRODUCTS));

        // 옵션이 없는 상품일 경우, 상품의 기본가격으로 비교합니다.
        if (cart.getOptionNumber() == null) {
            Goods goods = goodsRepository.findById(cart.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_GOODS));
            if (cart.getTotalPrice() != (goods.getPrice() * cart.getTotalAmount()))
                return true;
        }
        // 상품의 옵션이 변경되었으면 기존의 option 은 삭제되고 재생성됩니다.
        // 그러므로, cart 의 option_number 와 같은 Option_Id 의 존재여부로 비교합니다.
        if (cart.getOptionNumber() != null) {
            if (optionRepository.findById(cart.getOptionNumber()).isEmpty())
                result = true;
        }

        return result;
    }

    // 장바구니 수량, 옵션 변경
    @Override
    public void editCartItem(Long cartId, CartEditRequest cartEditRequest) {

        Member member = tokenCheckMember();

        Cart cart = cartRepository.findByIdAndMember(cartId, member).orElseThrow(
                () -> new BusinessException(NOT_FOUND_CART));

        Option option = optionRepository.findById(cartEditRequest.getOptionNumber()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_OPTION));

        cart.edit(option, cartEditRequest);
    }

    // 장바구니 상품 삭제
    @Override
    public void cartDeleteGoods(Long cartId, Long goodsId) {

        Member member = tokenCheckMember();

        Cart cart = cartRepository.findByIdAndGoodsIdAndMemberId(cartId, goodsId, member.getId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));

        cartRepository.deleteById(cart.getId());
    }

    private Member tokenCheckMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));
        return member;
    }
}
