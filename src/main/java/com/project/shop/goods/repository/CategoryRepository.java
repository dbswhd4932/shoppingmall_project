package com.project.shop.goods.repository;

import com.project.shop.goods.domain.enetity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}