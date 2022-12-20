package com.project.shop.member.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Role(RoleType roleType, Member member) {
        this.roleType = roleType;
        this.member = member;
    }

    @Override
    public String toString() {
        return roleType.role;
    }
}
