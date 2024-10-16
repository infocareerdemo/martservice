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

import com.mart.entity.Cart;
import com.mart.entity.Location;
import com.mart.service.CartService;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
	
	@Autowired
	CartService cartService;
	
	@PostMapping("/addProductByCart")
	public ResponseEntity<Object> addProductByCart(@RequestBody Cart cartReq)throws Exception{
		return new ResponseEntity<Object>(cartService.addProductByCart(cartReq), HttpStatus.OK);
		
	}
	
	
	@GetMapping("/getAllProductsByUserId")
	public ResponseEntity<Object> getAllProductsByUserId(@RequestParam Long userId)throws Exception{
		return new ResponseEntity<Object>(cartService.getAllProductsByUserId(userId), HttpStatus.OK);
		
	}
	
	

}
