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

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        Goods goods = goodsRepository.findById(cartCreateRequest.getGoodsId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));

        // 장바구니에 원래 있는 상품이면 개수, 금액 추가
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        if ( cartList.size() != 0 ) {
            for (Cart cart : cartList) {
                if ( cart.getGoodsId().equals(goods.getId())) {
                    cart.addAmount(cart, goods,cartCreateRequest);
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

    // 장바구니 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> cartFindAll() {
        return cartRepository.findAll()
                .stream().map(CartResponse::toCartResponse).collect(Collectors.toList());
    }

    // 장바구니 회원별 조회
    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> cartFind(Long memberId) {
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        return cartList.stream().map(CartResponse::toCartResponse).collect(Collectors.toList());

    }

    // 장바구니 상품 삭제
    @Override
    public void cartDeleteGoods(Long goodsId, Long memberId) {
        List<Cart> cartList = cartRepository.findByMemberId(memberId);
        // 장바구니 상품 존재여부 확인
        boolean check = false;
        for (Cart cart : cartList) {
            if (cart.getGoodsId().equals(goodsId)) {
                cartRepository.delete(cart);
                check = true;
            }
        }
        // 장바구니를 모두 체크했는데, 삭제할 상품이 없으면 예외
        if ( check == false) {
            throw new BusinessException(ErrorCode.NOT_FOUND_GOODS);
        }
    }
}
