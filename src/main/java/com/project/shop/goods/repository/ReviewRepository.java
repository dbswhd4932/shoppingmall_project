package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 페이징은 페치조인을 사용할 수 없으므로 join 으로 진행.
    @Query("select r from Review r join r.replies rp ")
    Page<Review> findAllByGoods(Goods goods, Pageable pageable);

}
