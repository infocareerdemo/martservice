package com.mart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "razorpay_payment")
@Data
public class RazorpayPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "razorpay_payment_id")
	private Long razorpayPaymentId;
	
	@OneToOne
	@JoinColumn(name = "razorpay_order_id", referencedColumnName = "razorpay_order_id", nullable = false)
	private RazorPayDetails razorPayDetails;
	
	@Column(name = "payment_id", nullable = false)
	private String paymentId;
	
	@Column(name = "signature", nullable = false)
	private String signature;
	
	@Column(name = "success", nullable = false)
	private boolean success;
		
	@ManyToOne
	@JoinColumn(name = "orders_id", referencedColumnName = "id", nullable = false)
	private Orders orders;
	
	@Column(name = "payment_date_time", nullable = false)
	private LocalDateTime paymentDateTime;
}