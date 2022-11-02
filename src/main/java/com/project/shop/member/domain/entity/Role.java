package com.project.shop.member.domain.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "role")
@Entity
public class Role {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Long id;          //권한번호

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;      //회원(다대일)

    @Column(nullable = false)
    private String roleType;    //권한종류(USER, SELLER, ADMIN)



}
