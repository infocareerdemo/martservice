package com.mart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.dto.WalletRequest;
import com.mart.exception.ApplicationException;
import com.mart.service.WalletService;



@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
	
	@Autowired
	WalletService walletService;

	
	@PostMapping("/payByWallet")
	public ResponseEntity<Object> payByWallet(@RequestBody WalletRequest walletRequest) throws Exception {
		return new ResponseEntity<Object>(walletService.payByWallet(walletRequest), HttpStatus.OK);
	}
	
	

	@PostMapping("/getOrderDetails")
	public ResponseEntity<Object> getOrderDetails(@RequestParam Long userId, @RequestParam Long orderId) throws ApplicationException {
		return new ResponseEntity<Object>(walletService.getOrderDetails(userId, orderId), HttpStatus.OK);
	}

}
