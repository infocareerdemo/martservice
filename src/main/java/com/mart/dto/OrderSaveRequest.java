package com.mart.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderSaveRequest {
    private List<OrderRequest> orderRequests; // List of order requests
    private PaymentRequest paymentRequest; // Payment information
    private Long userId; // User ID
    private Long locationId; // Location ID
}
