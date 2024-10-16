package com.mart.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderSaveRequest {
    private List<OrderRequest> orderRequests; 
    private PaymentRequest paymentRequest; 
    private Long userId; 
    private Long locationId; 
}
