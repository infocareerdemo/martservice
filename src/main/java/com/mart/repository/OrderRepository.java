package com.mart.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.Orders;



public interface OrderRepository extends JpaRepository<Orders, Long> {

	Orders findByOrderId(String oid);

	List<Orders> findByOrderedDateTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Orders> findByUserDetailUserId(Long userId);
    
   
	List<Orders> findByOrderedDateTimeBetweenAndLocationLocationId(LocalDateTime startOfDay, LocalDateTime endOfDay,
			Long locationId);

}
