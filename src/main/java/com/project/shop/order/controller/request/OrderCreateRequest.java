package com.project.shop.order.controller.request;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    @NotNull(message = "회원번호를 입력하세요.")
    private Long memberId;

    @NotNull(message = "수취인 이름을 입력하세요.")
    private String name;

    @NotNull(message = "수취인 전화번로를 입력하세요.")
    private String phone;

    @NotNull(message = "수취인 우편번호를 입력하세요.")
    private String zipcode;

    @NotNull(message = "수취인 상세주소를 입력하세요.")
    private String detailAddress;

    private String requirement;


}
