package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    // 상품 검색 - 상품이름 사용
    Optional<Goods> findByGoodsName(String goodsName);

    // 상품 검색 - 키워드
    List<Goods> findGoodsByGoodsNameContaining(String keyword);


}
