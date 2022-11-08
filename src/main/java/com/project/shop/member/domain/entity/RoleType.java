package com.project.shop.member.domain.entity;

public enum RoleType {

    ROLE_USER("일반사용자"),
    ROLE_SELLER("판매자"),
    ROLE_ADMIN("관리자");

    private String description;

    RoleType(String description) {
        this.description = description;
    }


}
