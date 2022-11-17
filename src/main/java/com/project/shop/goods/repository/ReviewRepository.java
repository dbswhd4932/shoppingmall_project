package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMemberId(Long memberId);
}
