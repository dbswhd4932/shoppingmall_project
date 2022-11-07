package com.project.shop.goods.repository;

import com.project.shop.goods.domain.enetity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
}
