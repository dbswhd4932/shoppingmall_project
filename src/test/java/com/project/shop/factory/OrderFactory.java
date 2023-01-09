package com.project.shop.factory;

import com.project.shop.goods.domain.Goods;
import com.project.shop.member.domain.Member;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.domain.Order;

import java.util.List;

public class OrderFactory {

    public static Order order(Member member) {
        return Order.builder()
                .memberId(member.getId())
                .name("name")
                .phone("010-1111-111")
                .zipcode("zipcode")
                .detailAddress("detailAddress")
                .requirement("requirement")
                .totalPrice(10000)
                .impUid("imp_1111")
                .merchantId("1111")
                .build();
    }

    public static OrderCreateRequest orderCreateRequest(Goods goods) {
        return OrderCreateRequest.builder()
                .orderItemCreates(List.of(OrderCreateRequest.orderItemCreate.builder()
                        .goodsId(goods.getId()).amount(10).orderPrice(10000).build()))
                .name("name")
                .phone("010-1234-1234")
                .zipcode("zipcode")
                .detailAddress("detailAddress")
                .requirement("requirement")
                .totalPrice(10000)
                .impUid("imp_12341234")
                .merchantId("123412341234")
                .cardCompany("국민은행")
                .cardNumber("1111-1111-1111-1111")
                .build();
    }

}