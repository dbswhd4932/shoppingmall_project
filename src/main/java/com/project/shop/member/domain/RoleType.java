package com.project.shop.member.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoleType {
    ROLE_USER("ROLE_USER"),
    ROLE_SELLER("ROLE_SELLER"),
    ROLE_ADMIN("ROLE_ADMIN");

    String role;

    RoleType(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }

    @JsonCreator
    public static RoleType from(String value) {
        // "SELLER" 또는 "ROLE_SELLER" 모두 처리
        if (value == null) {
            return null;
        }

        String upperValue = value.toUpperCase();

        // "ROLE_" prefix가 없으면 추가
        if (!upperValue.startsWith("ROLE_")) {
            upperValue = "ROLE_" + upperValue;
        }

        for (RoleType roleType : RoleType.values()) {
            if (roleType.role.equals(upperValue)) {
                return roleType;
            }
        }

        throw new IllegalArgumentException("Unknown role type: " + value);
    }

}
