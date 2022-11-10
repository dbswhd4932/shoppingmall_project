package com.project.shop.member.repository;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.response.CardResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByMemberId(Long memberId);
}