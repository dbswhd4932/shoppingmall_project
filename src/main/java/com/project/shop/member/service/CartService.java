package com.project.shop.member.service;

import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;

import java.util.List;

public interface CartService {

    // 장바구니 담기
    void cartAddGoods(CartCreateRequest cartCreateRequest);

    // 장바구니 조회
    List<CartResponse> cartFindMember();

    // 상품 변경 여부 확인(True , False)
    boolean checkGoodsInfoChange(Long goodsId);

    // 장바구니 수정
    void editCartItem(Long cartId, CartEditRequest cartEditRequest);

    // 장바구니 상품 삭제
    void cartDeleteGoods(Long cartId, Long goodsId);


}
