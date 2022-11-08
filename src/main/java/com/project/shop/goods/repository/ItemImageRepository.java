package com.project.shop.goods.repository;

import com.project.shop.goods.domain.enetity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepository extends JpaRepository<Image, Long> {
}
