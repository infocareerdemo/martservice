package com.mart.dto;

import lombok.Data;

@Data
public class WalletResponse {
	

	private double amount;         
    private double remainingAmount; 
    private double pendingAmount;
    
    private String message;
    private boolean success; 
	

}
