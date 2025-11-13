package com.project.shop.goods.repository;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.GoodsSearchCondition;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.shop.goods.domain.QCategory.category1;
import static com.project.shop.goods.domain.QGoods.goods;

public class GoodsRepositoryImpl implements GoodsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final CategoryRepository categoryRepository;

    public GoodsRepositoryImpl(EntityManager em, CategoryRepository categoryRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Goods> searchBetweenPrice(GoodsSearchCondition condition, Pageable pageable) {

        List<Goods> content = queryFactory
                .selectFrom(goods)
                .innerJoin(goods.category, category1).fetchJoin()
                .where(
                        betweenPrice(condition.getPriceMin(), condition.getPriceMax()),
                        categoryEq(condition))
                .orderBy(goods.price.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int total = content.size();
        return new PageImpl<>(content, pageable, total);

    }

    private BooleanExpression categoryEq(GoodsSearchCondition condition) {
        Category findCategory = categoryRepository.findByCategory(condition.getCategory()).get();

        // 화면에서 카테고리 목록을 선택할 수 있다 -> null 경우 없음
        return goods.category.eq(findCategory);
    }

    private BooleanExpression betweenPrice(Integer priceMin, Integer priceMax) {
        // 상품의 최대값을 어떻게 구하지??
        int maxPrice = 999999999;

        // null 원 이상 000원 이하
        if (priceMin == null && priceMax != null) {
            return priceLoe(priceMax).and(priceGoe(0));
            // 000원 이상 null 원 이하
        } else if (priceMin != null && priceMax == null) {
            return priceLoe(maxPrice).and(priceGoe(priceMin));
            // null 원 이상 null 원 이하
        } else if (priceMin == null && priceMax == null) {
            return priceLoe(maxPrice).and(priceGoe(0));
        } else {
            // 000원 이상 000원 이하
            return priceLoe(priceMax).and(priceGoe(priceMin));
        }
    }

    private BooleanExpression priceLoe(Integer priceMin) {
        return priceMin != null ? goods.price.loe(priceMin) : null;
    }

    private BooleanExpression priceGoe(Integer priceMax) {
        return priceMax != null ? goods.price.goe(priceMax) : null;
    }


}
