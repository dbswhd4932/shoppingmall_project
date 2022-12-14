package com.project.shop.member.domain;

public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_SELLER("ROLE_ANONYMOUS"),
    ROLE_ADMIN("ROLE_ADMIN");

    String role;

    Role(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }
}
