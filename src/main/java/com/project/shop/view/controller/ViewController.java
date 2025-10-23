package com.project.shop.view.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ViewController {

    /**
     * 홈 페이지
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/signup")
    public String signup() {
        return "member/signup";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/mypage")
    public String mypage() {
        return "member/mypage";
    }

    /**
     * 상품 목록 페이지
     */
    @GetMapping("/goods")
    public String goodsList() {
        return "goods/list";
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/goods/{goodsId}")
    public String goodsDetail(@PathVariable Long goodsId, Model model) {
        model.addAttribute("goodsId", goodsId);
        return "goods/detail";
    }

    /**
     * 상품 등록 페이지 (판매자)
     */
    @GetMapping("/goods/register")
    public String goodsRegister() {
        return "goods/register";
    }

    /**
     * 장바구니 페이지
     */
    @GetMapping("/cart")
    public String cart() {
        return "cart/list";
    }

    /**
     * 주문 페이지
     */
    @GetMapping("/order")
    public String order() {
        return "order/order";
    }

    /**
     * 주문 내역 페이지
     */
    @GetMapping("/orders")
    public String orderList() {
        return "order/list";
    }

    /**
     * 주문 상세 페이지
     */
    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "order/detail";
    }
}
