package com.project.shop.order.controller.response;

import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long memberId;          //회원번호

    private String name;            //수취인 이름
    private String phone;           //수취인 전화번호
    private String zipcode;         //우편번호
    private String detailAddress;   //상세주소
    private String requirement;     //요청사항
    private int totalPrice;         //결제금액
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;

    public static OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .memberId(order.getMemberId())
                .name(order.getName())
                .phone(order.getPhone())
                .zipcode(order.getZipcode())
                .detailAddress(order.getDetailAddress())
                .requirement(order.getRequirement())
                .totalPrice(order.getTotalPrice())
                .orderTime(order.getCratedAt())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
