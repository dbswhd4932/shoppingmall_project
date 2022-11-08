package com.project.shop.goods.repository;

import com.project.shop.goods.domain.enetity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemReviewRepository extends JpaRepository<Review, Long> {
}
