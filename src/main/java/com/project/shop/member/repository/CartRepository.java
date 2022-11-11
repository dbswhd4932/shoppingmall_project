package com.project.shop.member.repository;

import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository  extends JpaRepository<Cart, Long> {

    List<Cart> findByMemberId(Long memberId);

}
