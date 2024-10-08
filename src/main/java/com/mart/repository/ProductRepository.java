package com.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.Product;



public interface ProductRepository extends JpaRepository<Product, Long> {

	
//	Products findByProductName(String name);
//
//	List<Products> findByProductActive(boolean b);
//
//	Product findByProductNameAndLocationLocationId(String productName, Long locId);
//
//	Product findByProductNameAndLocationLocationIdAndProductIdNotIn(String productName, Long locId, List<Long> id);
//
//	List<Product> findByLocationLocationId(Long id);
//
//	List<Products> findByProductActiveTrue();
//
	//List<Product> findByLocationLocationIdAndProductActive(Long locationId, boolean b);

}
