package com.project.shop.goods.repository;

import com.project.shop.goods.controller.request.GoodsSearchCondition;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.QGoodsResponse;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.project.shop.goods.domain.QGoods.goods;
import static org.springframework.util.StringUtils.hasText;

public class GoodsRepositoryImpl implements GoodsRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final GoodsRepository goodsRepository;

    public GoodsRepositoryImpl(EntityManager em, GoodsRepository goodsRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.goodsRepository = goodsRepository;
    }

    @Override
    public List<GoodsResponse> searchCategoryAndBetweenPrice(GoodsSearchCondition condition) {
        return queryFactory
                .select(new QGoodsResponse(goods))
                .from(goods)
                .where(categoryEq(condition.getCategoryName()),
                        betweenPrice(condition.getPriceMin(), condition.getPriceMax()))
                .fetch();

    }

    private BooleanExpression categoryEq(String categoryName) {
        return hasText(categoryName) ? goods.category.eq(new Category(categoryName)) : null;
    }

    private BooleanExpression betweenPrice(Integer priceMin, Integer priceMax) {
        List<Goods> goodsList = goodsRepository.findAll();
        Long maxPrice = 0L;
        for (Goods good : goodsList) {
            long max = Math.max(maxPrice, good.getPrice());
            maxPrice = max;
        }

        if (priceMin == null && priceMax != null) {
            return priceLoe(priceMax).and(priceGoe(0));
        } else if (priceMin != null && priceMax == null) {
            return priceLoe(Math.toIntExact(maxPrice)).and(priceGoe(priceMin));
        } else {
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
