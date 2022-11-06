package com.project.shop.member.domain.request;

import com.project.shop.member.domain.entity.Address;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class MemberUpdateDto {

    private String password;
    private Address address;
    private String email;
    private String phone;

    @Builder
    public MemberUpdateDto(String password, Address address, String email, String phone) {
        this.password = password;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }
}
