package com.project.shop.member.controller;

import com.project.shop.member.domain.request.RoleCreateRequest;
import com.project.shop.member.domain.response.RoleResponse;
import com.project.shop.member.service.Impl.RoleServiceImpl;
import com.project.shop.member.service.RoleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoleController {

    private final RoleServiceImpl roleService;

    // 권한 생성
    @PostMapping("/roles")
    public void roleCreate(@RequestBody RoleCreateRequest roleCreateRequest) {
        roleService.roleCreate(roleCreateRequest);
    }

    // 권한 전체 조회
    @GetMapping("/roles")
    public List<RoleResponse> roleFindAll() {
        return roleService.roleFindAll();
    }

    // 권한 삭제
    @DeleteMapping("/roles/{roleId}")
    public void roleDelete(@PathVariable("roleId") Long roleId) {
        roleService.roleDelete(roleId);
    }





}
