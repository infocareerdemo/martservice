package com.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.RazorPayDetails;



public interface RazorPayDetailsRepository extends JpaRepository<RazorPayDetails, Long> {

	RazorPayDetails findByRazorpayOrderId(String razorpayOrderId);


}
