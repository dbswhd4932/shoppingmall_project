package com.project.shop.order.controller.response;

import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;           //주문번호
    private Long memberId;          //회원번호
    private List<Long> goodsId;    //주문한 상품 1~n개
    private String name;            //수취인 이름
    private String phone;           //수취인 전화번호
    private String zipcode;         //우편번호
    private String detailAddress;   //상세주소
    private String requirement;     //요청사항
    private String merchantId;      //주문번호 UUID
    private String orderNumber;     //주문번호 표시용
    private int totalPrice;         //결제금액
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .memberId(order.getMemberId())
                .name(order.getName())
                .phone(order.getPhone())
                .zipcode(order.getZipcode())
                .detailAddress(order.getDetailAddress())
                .requirement(order.getRequirement())
                .merchantId(order.getMerchantId())
                .orderNumber(order.getOrderNumber())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .orderTime(order.getCratedAt())
                .build();
    }
}
