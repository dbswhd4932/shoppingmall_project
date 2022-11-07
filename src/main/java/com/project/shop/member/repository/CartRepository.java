package com.project.shop.member.repository;

import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository  extends JpaRepository<Cart, Long> {

    // 회원의 카트가 있는지 확인
    Optional<Cart> findCartByMember(Member member);
}
