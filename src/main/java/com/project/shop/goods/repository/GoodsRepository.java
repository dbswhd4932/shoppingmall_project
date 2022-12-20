package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Goods;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    Optional<Goods> findByGoodsName(String goodsName);

    List<Goods> findGoodsByGoodsNameContaining(Pageable pageable, String keyword);

    Optional<Goods> findByMemberId(Long memberId);

    List<Goods> findAllByMemberId(Long memberId);
}
