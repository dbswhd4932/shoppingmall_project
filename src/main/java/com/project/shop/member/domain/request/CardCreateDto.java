package com.project.shop.member.domain.request;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CardCreateDto {

    // todo 질문 Long memberId 로 찾아야하나??
    private String cardCompany;
    private String cardNumber;
    private String cardExpire;

}
