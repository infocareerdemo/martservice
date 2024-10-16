package com.mart.dto;

import com.mart.entity.Product;

import lombok.Data;

@Data
public class ItemCount {

	private Product products;
	
	private long count;
}
