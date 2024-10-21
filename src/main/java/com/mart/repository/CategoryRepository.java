package com.mart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mart.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByProductsProductId(Long productId);

	Category  findByCategoryName(String categoryName);
	
	 List<Category> findAll();
	 



}

