package com.mart.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mart.entity.Orders;



public interface OrderRepository extends JpaRepository<Orders, Long> {

	Orders findByOrderId(String oid);

	List<Orders> findByOrderedDateTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Orders> findByUserDetailUserId(Long userId, Sort sort);
    
   
	List<Orders> findByOrderedDateTimeBetweenAndLocationLocationId(LocalDateTime startOfDay, LocalDateTime endOfDay,
			Long locationId);

	/*List<Orders> findByOrderedDateTimeAndPaymentStatus(LocalDateTime startDateTime, LocalDateTime endDateTime,
			String paymentStatus);*/
	
   
    @Query(value = "SELECT * FROM Orders WHERE ordered_date_time >= :startDateTime AND ordered_date_time < :endDateTime AND payment_status = :paymentStatus", nativeQuery = true)
    List<Orders> findByOrderedDateTimeAndPaymentStatus(
        @Param("startDateTime") LocalDateTime startDateTime, 
        @Param("endDateTime") LocalDateTime endDateTime, 
        @Param("paymentStatus") String paymentStatus
    );
    
}
