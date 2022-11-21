package com.project.shop.member.controller;

import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    // 장바구니 담기
    @PostMapping("/carts")
    @ResponseStatus(HttpStatus.CREATED)
    public void cartAddGoods(@RequestBody @Valid CartCreateRequest request, Long memberId) {
        cartService.cartAddGoods(request, memberId);
    }

    // 장바구니 조회
    @GetMapping("/carts")
    @ResponseStatus(HttpStatus.OK)
    public List<CartResponse> cartFind(Long memberId) {
        return cartService.cartFindMember(memberId);
    }

    // 장바구니 상품 선택 삭제
    @DeleteMapping("/carts/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    public void cartDeleteGoods(@PathVariable("cartId") Long cartId, Long goodsId) {
        cartService.cartDeleteGoods(cartId,goodsId);
    }
}
