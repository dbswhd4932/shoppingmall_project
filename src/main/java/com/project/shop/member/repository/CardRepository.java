package com.project.shop.member.repository;

import com.project.shop.member.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByMemberId(Long memberId);
}