package com.project.shop.goods.repository;

import com.project.shop.goods.controller.request.GoodsSearchCondition;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.QGoodsResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.shop.goods.domain.QGoods.goods;

public class GoodsRepositoryImpl implements GoodsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GoodsRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<GoodsResponse> searchBetweenPrice(GoodsSearchCondition condition, Pageable pageable) {
        List<GoodsResponse> content = queryFactory
                .select(new QGoodsResponse(goods))
                .from(goods)
                .where(betweenPrice(condition.getPriceMin(), condition.getPriceMax()))
                .orderBy(goods.price.desc()) // 상품 가격 내림차순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int total = content.size();
        return new PageImpl<>(content, pageable, total);

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
