package com.mart.dto;
import java.time.LocalDateTime;
import java.util.List;

import com.mart.entity.Location;

import lombok.Data;


@Data
public class ProductRequestDto {

	private Long productId;

	private String productName;


	private String productDescription;

	private double productPrice;
	
	private int productGST;

	private boolean productActive;
	
	private Location location;
	
	private byte[] productImage;

	private LocalDateTime updatedDate;
	
	private Long productUpdatedBy;
	
    private List<Long> categoryIds;


}
