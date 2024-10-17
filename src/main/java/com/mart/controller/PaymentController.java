package com.mart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.dto.PaymentRequest;
import com.mart.entity.RazorpayPayment;
import com.mart.exception.ApplicationException;
import com.mart.service.PaymentService;


@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

	@Autowired
	PaymentService paymentService;
	
	

	/*@PostMapping("/createOrder")
	public ResponseEntity<Object> createOrder(@RequestParam Long id, @RequestParam Long oid) throws ApplicationException {
		return new ResponseEntity<Object>(paymentService.createOrder(id, oid), HttpStatus.OK);
	}*/
	

	@PostMapping("/createOrder")
	public ResponseEntity<Object> createOrder(@RequestParam Long id, @RequestParam Long oid,@RequestParam double razorpayAmount) throws ApplicationException {
		return new ResponseEntity<Object>(paymentService.createOrder(id, oid,razorpayAmount), HttpStatus.OK);
	}
	
	@PostMapping("/checkStatus")
	public ResponseEntity<Object> checkPaymentStatus(@RequestBody RazorpayPayment razorpayPayment) {
		return new ResponseEntity<Object>(paymentService.isPaymentSuccess(razorpayPayment), HttpStatus.OK);
	}
	
	
	@PostMapping("/payAmount")
	public ResponseEntity<Object> payWalletAndCash(@RequestParam Long userId, @RequestParam Long orderId, @RequestBody PaymentRequest paymentRequest) throws ApplicationException {
		return new ResponseEntity<Object>(paymentService.payAmount(userId,orderId,paymentRequest), HttpStatus.OK);
	}
}
