package com.mart.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;
import lombok.Data;


@Data
public class CategoryRequestDto {
	
	   
	   @Nullable
	     private Long   categoryId;
	   
	   @Nullable
		 private String categoryName;
	   
	   @Nullable
	   private List<Long> productIds = new ArrayList<>(); 

}
