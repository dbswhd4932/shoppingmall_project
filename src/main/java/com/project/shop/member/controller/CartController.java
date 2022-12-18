package com.project.shop.member.controller;

import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    // 장바구니 담기
    @PostMapping("/carts/{memberId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER')")
    public void cartAddGoods(@PathVariable("memberId") Long memberId, @RequestBody @Valid CartCreateRequest request) {
        cartService.cartAddGoods(request, memberId);
    }

    // 장바구니 조회
    @GetMapping("/carts/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER')")
    public List<CartResponse> cartFind(@PathVariable("memberId") Long memberId) {
        return cartService.cartFindMember(memberId);
    }

    // 상품 변경 여부 확인 - 프론트
    @GetMapping("/carts/{cartId}/check")
    @ResponseStatus(HttpStatus.OK)
    public boolean checkGoodsInfoChange(@PathVariable("cartId") Long cartId) {
        return cartService.checkGoodsInfoChange(cartId);
    }

    // 장바구니 수정
    @PutMapping("/carts/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER')")
    public void cartEdit(@PathVariable("cartId") Long cartId, @RequestBody CartEditRequest cartEditRequest) {
        cartService.editCartItem(cartId, cartEditRequest);
    }

    // 장바구니 상품 선택 삭제
    @DeleteMapping("/carts/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER')")
    public void cartDeleteGoods(@PathVariable("cartId") Long cartId, Long goodsId, Long memberId) {
        cartService.cartDeleteGoods(cartId, goodsId, memberId);
    }
}
