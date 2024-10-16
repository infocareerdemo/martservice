package com.mart.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class OrderDashboard {

	private Long totalOrdersCount;
	
	private List<ItemCount> totalOrderDetails;
	
	private Map<String, Long> locationOrderDetails;
}
