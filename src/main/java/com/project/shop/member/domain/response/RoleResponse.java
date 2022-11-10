package com.project.shop.member.domain.response;


import com.project.shop.member.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private String role;

    public RoleResponse(Role role) {
        this.role = role.getRole();
    }
}
