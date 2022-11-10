package com.project.shop.member.service.Impl;

import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.entity.Role;
import com.project.shop.member.domain.request.RoleCreateRequest;
import com.project.shop.member.domain.response.RoleResponse;
import com.project.shop.member.repository.RoleRepository;
import com.project.shop.member.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    // 권한생성
    @Override
    public void roleCreate(RoleCreateRequest roleCreateRequest) {
        Role role = Role.builder()
                .role(roleCreateRequest.getRole())
                .build();
        roleRepository.save(role);
    }

    // 권한 전체조회
    @Transactional(readOnly = true)
    @Override
    public List<RoleResponse> roleFindAll() {
        return roleRepository.findAll()
                .stream().map(RoleResponse::new).collect(Collectors.toList());
    }

    // 권한 삭제
    @Override
    public void roleDelete(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        roleRepository.delete(role);
    }
}