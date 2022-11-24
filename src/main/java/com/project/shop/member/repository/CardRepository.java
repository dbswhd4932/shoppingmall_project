package com.project.shop.member.repository;

import com.project.shop.member.domain.Card;
import com.project.shop.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByMemberId(Long memberId);

    Optional<Card> findByIdAndMemberId(Long cardId, Long memberId );
}