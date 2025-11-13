package com.project.shop.member.controller;

import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
// import com.project.shop.member.service.CartService;  // MySQL 버전 (주석처리)
import com.project.shop.member.service.RedisCartService;  // Redis 버전으로 전환
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

/**
 * 장바구니 컨트롤러 (Redis 버전)
 *
 * 변경 사항:
 * - CartService (MySQL) → RedisCartService (Redis)로 교체
 * - 삭제 API: cartId → goodsId + optionNumber로 변경
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    // private final CartService cartService;  // MySQL 버전 (주석처리)
    private final RedisCartService redisCartService;  // Redis 버전

    // 장바구니 담기
    @PostMapping("/carts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 상품 추가 (Redis)")
    public void cartAddGoods(@RequestBody @Valid CartCreateRequest cartCreateRequest) {
        // cartService.cartAddGoods(cartCreateRequest);  // MySQL 버전
        redisCartService.addToCart(cartCreateRequest);   // Redis 버전
    }

    // 장바구니 조회
    @GetMapping("/carts")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 조회 (Redis)")
    public Page<CartResponse> cartFind(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // return cartService.cartFindMember(pageable);  // MySQL 버전
        return redisCartService.getCartItems(pageable);  // Redis 버전
    }

    // 장바구니 수정
    @PutMapping("/carts/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 상품 수정 (Redis)")
    public void cartEdit(@PathVariable("goodsId") Long goodsId, @RequestBody CartEditRequest cartEditRequest) {
        // cartService.editCartItem(cartId, cartEditRequest);  // MySQL 버전 (cartId 사용)
        redisCartService.updateCartItem(goodsId, cartEditRequest);  // Redis 버전 (goodsId 사용)
    }

    // 장바구니 상품 선택 삭제
    @DeleteMapping("/carts/{goodsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "장바구니 상품 삭제 (Redis)")
    public void cartDeleteGoods(
            @PathVariable("goodsId") Long goodsId,
            @RequestParam(required = false) Long optionNumber) {
        // cartService.cartDeleteGoods(cartId);  // MySQL 버전 (cartId 사용)
        redisCartService.removeFromCart(goodsId, optionNumber);  // Redis 버전 (goodsId + optionNumber 사용)
    }
}
