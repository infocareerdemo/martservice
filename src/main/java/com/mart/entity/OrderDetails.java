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
import lombok.Data;


@Data
@Entity
public class OrderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="order_details_id")
	private Long orderDetailsId;	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_conf_id", referencedColumnName = "id", nullable = false)
	private Orders orders;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ordered_product_id",referencedColumnName = "product_id", nullable = false)
	private Product products;
	
	@Column(name="quantity",nullable = false)
	private Long quantity;
	
	@Column(name="unit_price" ,nullable = false)
	private double unitPrice;
	
	@Column(name="total_price" ,nullable = false)
	private double totalPrice;
	
	@Column(name="order_date_time" ,nullable = false)
	private LocalDateTime orderDateTime;
}
