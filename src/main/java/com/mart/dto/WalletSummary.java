package com.mart.dto;

import java.util.List;

import com.mart.entity.OrderDetails;
import com.mart.entity.Orders;

import lombok.Data;

@Data
public class WalletSummary {
	
	public List<OrderDetails> orderDetails;

	public Orders orders;

}
