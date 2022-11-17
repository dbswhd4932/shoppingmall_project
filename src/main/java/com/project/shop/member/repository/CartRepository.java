package com.project.shop.member.repository;

import com.project.shop.member.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository  extends JpaRepository<Cart, Long> {

    List<Cart> findByMemberId(Long memberId);

}
