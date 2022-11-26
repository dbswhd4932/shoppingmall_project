package com.project.shop.order.controller.request;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    @NotNull
    private Long memberId;

    @NotBlank(message = "수취인 이름을 입력하세요.")
    private String name;

    @NotBlank(message = "수취인 전화번로를 입력하세요.")
    private String phone;

    @NotBlank(message = "수취인 우편번호를 입력하세요.")
    private String zipcode;

    @NotBlank(message = "수취인 상세주소를 입력하세요.")
    private String detailAddress;

    private String requirement;


}
