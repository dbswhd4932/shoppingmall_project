package com.project.shop.goods.repository;

import com.project.shop.goods.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategory(String categoryName);
}
