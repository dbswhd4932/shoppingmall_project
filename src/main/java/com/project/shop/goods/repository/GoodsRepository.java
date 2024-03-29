package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long>, GoodsRepositoryCustom {

    Optional<Goods> findByGoodsName(String goodsName);

    Page<Goods> findGoodsByGoodsNameContaining(Pageable pageable, String keyword);
    List<Goods> findAllByCategory(Category category);

    List<Goods> findAllByMemberId(Long memberId);
}
