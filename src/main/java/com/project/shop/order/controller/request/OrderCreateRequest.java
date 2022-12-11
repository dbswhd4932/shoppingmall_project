package com.project.shop.order.controller.request;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
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

    @NotBlank(message = "아임포트 결제 ID 를 입력하세요.")
    private String impUid;

    @NotBlank(message = "가맹점 ID 를 입력하세요.")
    private String merchantId;

    @NotNull(message = "카드사를 입력하세요.")
    private String cardCompany;

    @NotNull(message = "카드일련번호를 입력하세요.")
    private String cardNumber;

}
