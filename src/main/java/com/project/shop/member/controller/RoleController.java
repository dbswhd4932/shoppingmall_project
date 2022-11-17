package com.project.shop.member.controller;

import com.project.shop.member.controller.request.RoleCreateRequest;
import com.project.shop.member.controller.response.RoleResponse;
import com.project.shop.member.service.Impl.RoleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoleController {

    private final RoleServiceImpl roleService;

    // 권한 생성
    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public void roleCreate(@RequestBody RoleCreateRequest roleCreateRequest) {
        roleService.roleCreate(roleCreateRequest);
    }

    // 권한 전체 조회
    @GetMapping("/roles")
    @ResponseStatus(HttpStatus.OK)
    public List<RoleResponse> roleFindAll() {
        return roleService.roleFindAll();
    }

    // 권한 삭제
    @DeleteMapping("/roles/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public void roleDelete(@PathVariable("roleId") Long roleId) {
        roleService.roleDelete(roleId);
    }





}
