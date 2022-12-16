package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByGoodsId(Long goodsId);

    Optional<Option> findByIdAndGoodsId(Long optionId, Long goodsId);
}
