package com.project.shop.order.domain.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

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
}
