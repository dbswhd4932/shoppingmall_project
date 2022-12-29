package com.project.shop.member.controller.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class RoleCreateRequest {

    @NotNull(message = "권한을 입력하세요.")
    private String role;
}
