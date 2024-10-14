package com.mart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.OrderDetails;


public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long>{

	List<OrderDetails> findByOrdersId(Long id);

}
