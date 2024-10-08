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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name = "products") // Specify the table name
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id") // Specify the column name for the primary key
	private Long productId;

	@NotBlank(message = "Name is mandatory")
	@Size(max = 100, message = "Name must be less than 100 characters")
	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@Size(max = 255, message = "Description must be less than 255 characters")
	@Column(name = "product_description", length = 255, nullable = false)
	private String productDescription;

	@NotNull(message = "Price is mandatory")
	@Positive(message = "Price must be positive")
	@Column(name = "product_price", nullable = false)
	private double productPrice;
	
	@Column(name = "product_gst", nullable = false)
	private int productGST;

	@NotNull(message = "Active status is mandatory")
	@Column(name = "product_active", nullable = false)
	private boolean productActive;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Prodcut_location", referencedColumnName = "location_id", nullable = false)
	private Location location;
	
	@Column(name = "product_image")
	private byte[] productImage;

	@Column(name = "product_updated_date_time", nullable = false)
	private LocalDateTime updatedDate;
	
	@Column(name = "product_updated_by", nullable = false)
	private Long productUpdatedBy;

}
