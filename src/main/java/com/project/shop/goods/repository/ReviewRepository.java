package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByGoods(Goods goods, Pageable pageable);
}
