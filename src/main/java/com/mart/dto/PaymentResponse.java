package com.mart.dto;

import java.util.List;

import com.mart.entity.OrderDetails;
import com.mart.entity.Orders;
import com.mart.entity.RazorPayDetails;

import lombok.Data;

@Data
public class PaymentResponse {

	private List<OrderDetails> orderDetails;

	private Orders orders;	
	
	private double walletAmount;
    private double cashAmount;
    private double razorpayAmount;
    
    private RazorPayDetails razorPayDetails;
    
	private String successMsg;

}
