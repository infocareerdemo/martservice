package com.mart.dto;

import java.util.List;

import com.mart.entity.Orders;
import com.mart.entity.OrderDetails;

import lombok.Data;

@Data
public class OrderSummary {

	public List<OrderDetails> orderDetails;

	public Orders orders;
	
	public PaymentRequest paymentRequest;	
	}
