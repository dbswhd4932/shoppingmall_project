package com.project.shop.member.service;

import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartPageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    // 장바구니 담기
    void cartAddGoods(CartCreateRequest cartCreateRequest);

    // 장바구니 조회
    List<CartPageResponse> cartFindMember(Pageable pageable);

    // 장바구니 수정
    void editCartItem(Long cartId, CartEditRequest cartEditRequest);

    // 장바구니 상품 삭제
    void cartDeleteGoods(Long cartId);


}
