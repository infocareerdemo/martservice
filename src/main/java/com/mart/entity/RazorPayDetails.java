package com.mart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "razorpay_details")
@Data
public class RazorPayDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "razorpay_details_id")
	private Long razorpayDetailsId;
	
	@Column(name = "application_fee")
	private String applicationFee;

	// razorpay will generate the razorpay orderid, which will be used to pay the amount
	@Column(name = "razorpay_order_id", unique = true)
	private String razorpayOrderId;

	// note will create after the razorpay_order_id is generated
	@Column(name = "notes" , nullable = true )
	private String notes;

	@Column(name = "theme" , nullable = true)
	private String theme;
	
	@Column(name = "secret_id" , nullable = false)
	private String secretId;

	@Column(name = "merchant_name" , nullable = false)
	private String merchantName;

	@Column(name = "purchase_description" , nullable = true)
	private String purchaseDescription;

	@Column(name = "customer_contact" , nullable = false)
	private Long customerContact;
	
	@ManyToOne
	@JoinColumn(name = "order_conf_id",referencedColumnName = "id", nullable = true)
	private Orders orders;
	
	@Column(name = "razorpay_date_time")
	private LocalDateTime razorpayDateTime;
}