package com.mart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mart.entity.Category;
import com.mart.entity.Product;



public interface ProductRepository extends JpaRepository<Product, Long> {

	   Product findByProductNameAndLocationLocationId(String productName, Long locationId);

	    List<Product> findByLocationLocationId(Long locationId);

		List<Product> findByLocationLocationIdAndProductActive(Long locationId, boolean b);
		
		List<Category> findCategoriesByProductId(Long productId);

	    List<Product> findByCategoriesCategoryId(Long categoryId);
		
		Product findByProductId(Long productId);
		

}


