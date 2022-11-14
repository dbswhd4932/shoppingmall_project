package com.project.shop.goods.repository;

import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.domain.response.GoodsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    // 상품 검색 - 상품이름 사용
    Optional<Goods> findByGoodsName(String goodsName);

    // 상품 검색 - 키워드
    List<Goods> findGoodsByGoodsNameContaining(String keyword);


}
