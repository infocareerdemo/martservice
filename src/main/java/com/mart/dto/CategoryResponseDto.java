package com.mart.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.mart.entity.Location;

import lombok.Data;

@Data
public class CategoryResponseDto {
	
	private Long   categoryId;    	 
    private String categoryName;
	  private byte[] categoryImage;

	Set<ProductResponseDto> products;
	
	
	@Data
	public  static class ProductResponseDto{
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
		
		 private boolean productCategory;
		 
		   private Long   categoryId;    	 
		    private String categoryName;
	}

}