package com.mart.dto;

import lombok.Data;

@Data
public class OrderStatusDto {
	
	private String orderId;
	
	private boolean cashOrderStatus;
	
	private boolean deliveredStatus;



}
