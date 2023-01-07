package com.project.shop.member.repository;

import com.project.shop.member.domain.MemberLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLoginHistoryRepository extends JpaRepository<MemberLoginHistory, Long> {
}
