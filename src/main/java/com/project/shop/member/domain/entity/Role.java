package com.project.shop.member.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    @JsonIgnore
    private Long id;

    private String role; // todo USER, SELLER , ADMIN 을 고정으로 DB 에 저장하고 사용하도록 ..

    @Builder
    public Role(String role) {
        this.role = role;
    }
}
