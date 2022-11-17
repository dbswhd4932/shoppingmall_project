package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByGoodsId(Long goodsId);
}
