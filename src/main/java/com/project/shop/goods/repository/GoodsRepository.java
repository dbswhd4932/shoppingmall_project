package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long>, GoodsRepositoryCustom {

    Optional<Goods> findByGoodsName(String goodsName);

    Page<Goods> findGoodsByGoodsNameContaining(Pageable pageable, String keyword);
    List<Goods> findAllByCategory(Category category);
    Page<Goods> findAllByCategory(Category category, Pageable pageable);

    List<Goods> findAllByMemberId(Long memberId);

    // 장바구니 조회용: images를 함께 조회 (Lazy Loading 문제 해결)
    @Query("SELECT DISTINCT g FROM Goods g LEFT JOIN FETCH g.images WHERE g.id IN :ids")
    List<Goods> findAllByIdWithImages(@Param("ids") List<Long> ids);
}
