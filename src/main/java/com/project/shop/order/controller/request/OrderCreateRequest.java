package com.project.shop.order.controller.request;

import lombok.*;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    private Long memberId;
    private String name;
    private String phone;
    private String zipcode;
    private String detailAddress;
    private String requirement;


}
