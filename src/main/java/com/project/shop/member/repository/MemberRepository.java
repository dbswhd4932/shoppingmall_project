package com.project.shop.member.repository;

import com.project.shop.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String username);
    Optional<Member> findByLoginIdAndPassword(String loginId, String password);

}
