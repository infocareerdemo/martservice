package com.mart.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mart.entity.Product;
import com.mart.service.ProductService;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

	@Autowired
	ProductService productService;

	@PostMapping("/saveOrUpdateProduct")
	public ResponseEntity<Object> saveOrUpdateProduct(@ModelAttribute Product productReq , @RequestPart(required = false) MultipartFile productImg) throws Exception{	
		return new ResponseEntity<Object> (productService.saveOrUpdateProduct(productReq,productImg),HttpStatus.OK);
		
	}
	
	@GetMapping("/getAllProduct")
	public ResponseEntity<Object> getAllProduct()throws Exception{		
		return new ResponseEntity<Object> (productService.getAllProduct(),HttpStatus.OK);
		
	}
	
	@GetMapping("/getAllProductsByLocation")
	public ResponseEntity<Object> getAllProductsByLocation(@RequestParam Long id) throws Exception{		
		return new ResponseEntity<Object>(productService.getAllProductsByLocation(id), HttpStatus.OK);
		
	}
}
