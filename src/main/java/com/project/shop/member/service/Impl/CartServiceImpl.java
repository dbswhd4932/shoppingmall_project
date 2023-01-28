package com.project.shop.member.service.Impl;

import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Options;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Member member = getMember();
        Goods goods = ExistGoodsListCheck(cartCreateRequest.getGoodsId());

        // 옵션이 있는 상품일 경우 optionRepository 에서 찾아서 초기화 해줍니다.
        Options options = null;
        if (!optionRepository.findByGoodsId(cartCreateRequest.getGoodsId()).isEmpty()) {
            options = optionRepository.findByIdAndGoodsId(cartCreateRequest.getOptionNumber(), goods.getId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_OPTION));
        }

        // 장바구니에 이미 존재하는 상품이면 예외처리
        if (cartRepository.findByGoodsIdAndMember(goods.getId(), member).isPresent())
            throw new BusinessException(CART_IN_GOODS_DUPLICATED);

        int goodsTotalPrice = goods.getPrice();
        // 옵션이 있는 상품이면 상품 최종 가격변경(기본상품 + 옵션가격)
        if (!goods.getOptions().isEmpty()) {
            goodsTotalPrice = options.getTotalPrice();
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
    public Page<CartResponse> cartFindMember(Pageable pageable) {
        Member member = getMember();

        Page<Cart> carts = cartRepository.findAllByMemberId(member.getId(), pageable);
        return carts.map(CartResponse::new);


    }

    // 장바구니 수량, 옵션 변경
    @Override
    public void editCartItem(Long cartId, CartEditRequest cartEditRequest) {
        Member member = getMember();
        Cart cart = ExistMemberCartCheck(cartId, member);
        Goods goods = ExistGoodsListCheck(cart.getGoodsId());

        List<Options> options = optionRepository.findByGoodsId(goods.getId());

        // 옵션이 없는 상품
        if (options.isEmpty()) {
            if (cartEditRequest.getOptionNumber() != null) throw new BusinessException(NOT_FOUND_OPTION);
            cart.editCartExcludeOption(goods, cartEditRequest);
            return;
        }

        // 옵션 있는 상품
        for (Options option : options) {
            if (!option.getId().equals(cartEditRequest.getOptionNumber())) continue;
            cart.editCartIncludeOption(option, cartEditRequest);
            return;
        }
    }

    // 장바구니 상품 삭제
    @Override
    public void cartDeleteGoods(Long cartId) {
        Member member = getMember();
        Cart cart = ExistMemberCartCheck(cartId, member);
        cartRepository.deleteById(cart.getId());
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }

    // 상품 존재여부 확인
    private Goods ExistGoodsListCheck(Long goodsId) {
        return goodsRepository.findById(goodsId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_GOODS));
    }

    // 회원 장바구니 존재여부 확인
    private Cart ExistMemberCartCheck(Long cartId, Member member) {
        return cartRepository.findByIdAndMember(cartId, member).orElseThrow(() -> new BusinessException(NOT_FOUND_CART));
    }

}
