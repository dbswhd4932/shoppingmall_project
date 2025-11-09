package com.project.shop.member.controller;

import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.service.CartService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    // 장바구니 담기
    @PostMapping("/carts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 상품 추가")
    public void cartAddGoods( @RequestBody @Valid CartCreateRequest cartCreateRequest) {
        cartService.cartAddGoods(cartCreateRequest);
    }

    // 장바구니 조회
    @GetMapping("/carts")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 조회")
    public Page<CartResponse> cartFind(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return cartService.cartFindMember(pageable);
    }

    // 장바구니 수정
    @PutMapping("/carts/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 상품 수정")
    public void cartEdit(@PathVariable("cartId") Long cartId, @RequestBody CartEditRequest cartEditRequest) {
        cartService.editCartItem(cartId, cartEditRequest);
    }

    // 장바구니 상품 선택 삭제
    @DeleteMapping("/carts/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 상품 삭제")
    public void cartDeleteGoods(@PathVariable("cartId") Long cartId) {
        cartService.cartDeleteGoods(cartId);
    }
}
