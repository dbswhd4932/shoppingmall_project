package com.project.shop.member.domain.request;

import lombok.Getter;

@Getter
public class CardCreateRequest {

    private Long memberId;
    private String cardCompany;
    private String cardNumber;
    private String cardExpire;
}
