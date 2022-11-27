package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findAllByGoodsId(Long goodsId);
}
