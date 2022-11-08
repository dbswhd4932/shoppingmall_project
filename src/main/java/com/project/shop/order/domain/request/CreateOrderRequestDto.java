package com.project.shop.order.domain.request;

import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.member.domain.entity.Address;
import com.project.shop.member.domain.entity.Member;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDto {

    private Member member;
    private Goods goods;
    private String name;
    private String phone;
    private Address address;
    private String requirement;
    private int totalPrice;

}
