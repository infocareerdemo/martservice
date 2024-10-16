package com.mart.dto;

import java.util.List;

import lombok.Data;

@Data
public class WalletRequest {

	    private Long userId;
	    private double walletAmount; 
	    private Long orderId;
	    
	    private List<Long> productIds;
	    private boolean productActive;
	    

}
