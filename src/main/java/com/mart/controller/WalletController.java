package com.mart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.exception.ApplicationException;
import com.mart.service.WalletService;



@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
	
	@Autowired
	WalletService walletService;
	
	 
	 @GetMapping("/getWalletDetailsByUserId")
		public ResponseEntity<Object> getWalletDetailsByUserId(@RequestParam Long id) throws ApplicationException {
			return new ResponseEntity<Object>(walletService.getWalletDetailsByUserId(id), HttpStatus.OK);
		}

	 
	
	@PostMapping("/payByWallet")
	public ResponseEntity<Object> payByWallet(@RequestParam Long id ,@RequestParam double amount) throws Exception {
		return new ResponseEntity<Object>(walletService.payByWallet(id, amount), HttpStatus.OK);
	}

}
