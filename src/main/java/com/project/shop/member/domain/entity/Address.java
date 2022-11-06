package com.project.shop.member.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class Address {

    private String zipcode;     //우편번호
    private String detailAddress; //상세주소

    @Builder
    public Address(String zipcode, String detailAddress) {
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
    }
}
