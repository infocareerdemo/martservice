package com.mart.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderWithPaymentRequest {
    private List<OrderRequest> orderRequests;  // List of products with quantities
    private PaymentRequest paymentRequest;     // Payment details
}
