package com.project.shop.order.domain.response;

import com.project.shop.member.domain.entity.Member;
import com.project.shop.order.domain.request.CreateOrderRequestDto;
import lombok.Getter;

@Getter
public class OrderResponseDto {

    private Member member;
    private String name;
    private String phone;
    private Address address;
    private String requirement;
    private int totalPrice;

    public OrderResponseDto(CreateOrderRequestDto requestDto) {
        this.member = requestDto.getMember();
        this.name = requestDto.getName();
        this.phone = requestDto.getPhone();
        this.address = requestDto.getAddress();
        this.requirement = requestDto.getRequirement();
        this.totalPrice = requestDto.getTotalPrice();;
    }
}
