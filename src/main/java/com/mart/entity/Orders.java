package com.mart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "orders") 
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id") 
	private Long id;
	
	@Column(name = "order_id", unique = true)
	private String orderId;
	

	@Column(name = "order_amount")
	private Double orderAmount;
	
	@Column(name = "gst")
	private Double gst;
	
	@Column(name = "gst_amount")
	private Double gstAmount;
	
	@Column(name = "total_amount")
	private double totalAmount;
	
	@Column(name = "address")
	private String address;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_location", referencedColumnName = "location_id")
	private Location location;
	
	@Column(name = "payment_status")
	private String paymentStatus;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ordered_user",referencedColumnName = "user_id", nullable = false)
	private UserDetail userDetail;
	
	@Column(name = "ordered_date_time", nullable = false)
	private LocalDateTime orderedDateTime;
	
	@Column(name = "wallet_amount")
	private double walletAmount;
	
	@Column(name = "cash_amount")
	private double cashAmount;
	
	@Column(name = "razorpay_amount")
	private double razorpayAmount;
	
	@Column(name = "cash_order_status",nullable = true)
	private boolean cashOrderStatus;
	
	@Column(name = "delivered_status",nullable = true)
	private boolean deliveredStatus;
	
	
}
