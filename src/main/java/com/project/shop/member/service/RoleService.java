package com.project.shop.member.service;

import com.project.shop.member.controller.request.RoleCreateRequest;
import com.project.shop.member.controller.response.RoleResponse;

import java.util.List;

public interface RoleService {

    // 권한 생성
    void roleCreate(RoleCreateRequest roleCreateRequest);

    // 권한 전제 조회
    List<RoleResponse> roleFindAll();

    // 권한 삭제
    void roleDelete(Long roleId);

}
