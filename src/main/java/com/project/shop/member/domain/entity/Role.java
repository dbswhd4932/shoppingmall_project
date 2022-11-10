package com.project.shop.member.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.shop.member.domain.request.RoleCreateRequest;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @JsonIgnore
    private String role;

}
