package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Options;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Options, Long> {

    List<Options> findByGoodsId(Long goodsId);

    Optional<Options> findByIdAndGoodsId(Long optionId, Long goodsId);
}
