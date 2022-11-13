package com.project.shop.member.service;

import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CartCreateRequest;
import com.project.shop.member.domain.response.CartResponse;

import java.util.List;

public interface CartService {

    // 장바구니 담기
    void cartAddGoods(CartCreateRequest cartCreateRequest, Long memberId);

    // 장바구니 조회
    List<CartResponse> cartFindMember(Long memberId);

    // 장바구니 상품 삭제
    void cartDeleteGoods(Long cartId, Long goodsId);


}
