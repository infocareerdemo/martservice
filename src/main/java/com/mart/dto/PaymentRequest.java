package com.mart.dto;

import lombok.Data;

@Data
public class PaymentRequest {
	
	private double walletAmount;
    private double cashAmount;
    private double razorpayAmount;

}
