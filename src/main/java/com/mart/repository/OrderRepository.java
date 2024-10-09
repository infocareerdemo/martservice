package com.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.Order;



public interface OrderRepository extends JpaRepository<Order, Long> {
//
//	Orders findByOrderId(String oid);
//
//	List<Order> findByUserLoginUserId(Long id);
//
//	List<Orders> findByOrderedDateTimeBetweenAndLocationLocationId(LocalDateTime startOfDay, LocalDateTime endOfDay,
//			Long locationId);
//
//	List<Order> findByOrderedDateTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

}
