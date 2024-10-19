package com.mart.dto;

import java.util.List;

import lombok.Data;

@Data
public class CategoryRequestDto {
	
	     private Long   categoryId;
	 
		 private String categoryName;
		  
		 private List<Long> productIds; 


}
