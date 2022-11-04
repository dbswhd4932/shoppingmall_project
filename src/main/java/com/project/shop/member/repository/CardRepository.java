package com.project.shop.member.repository;

import com.project.shop.member.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Cart, Long> {
}
