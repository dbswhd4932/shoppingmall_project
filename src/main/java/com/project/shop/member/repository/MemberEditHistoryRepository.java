package com.project.shop.member.repository;

import com.project.shop.member.domain.MemberEditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberEditHistoryRepository extends JpaRepository<MemberEditHistory, Long> {
}
