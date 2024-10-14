package com.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.RazorpayPayment;


public interface RazorpayPaymentRepository extends JpaRepository<RazorpayPayment, Long> {

	
}
