package com.project.shop.member.repository;

import com.project.shop.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByEmailAndLoginId(String email, String loginId);
}
