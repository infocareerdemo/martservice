package com.mart.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "shopping_cart")
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id") 
	private Long cartId;
	
	@Column(name="quantity",nullable = false)
	private Long quantity;
	
	@Column(name="unit_price" )
	private double unitPrice;
	
	@Column(name="total_price" )
	private double totalPrice;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cart_user_id", referencedColumnName = "user_id", nullable = false) 
	private UserDetail userDetail;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cart_product_id" ,  referencedColumnName = "product_id", nullable = false)
	private Product product;
	
	@Column(name = "product_active")
	private boolean productActive;
	
	@Column(name="order_date_time")
	private LocalDateTime updatedDateTime;
}




	