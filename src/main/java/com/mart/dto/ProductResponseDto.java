package com.mart.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mart.entity.Location;

import lombok.Data;

@Data
public class ProductResponseDto {
	

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
	
    private Set<CategoryResponseDto> categories;
	
	
    @Data
    public static class CategoryResponseDto {  	
    private Long   categoryId;    	 
    private String categoryName;
    }  
	
 
}
