package com.project.shop.member.repository;

import com.project.shop.member.domain.Member;
import com.project.shop.member.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByMember(Member member);
}
