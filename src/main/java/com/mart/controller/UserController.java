package com.mart.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.config.JwtUtil;
import com.mart.dto.LoginDto;
import com.mart.entity.UserDetail;
import com.mart.service.UserDetailService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	@Autowired
	UserDetailService userDetailService;
	
	@Autowired
	JwtUtil jwtUtil;
	

	//Check employee code and send otp to phone
	@PostMapping("/verifyEmployeeCodeAndGenerateOtp")
	public ResponseEntity<Object> verifyEmployeeCodeAndGenerateOtp(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception{		
		return new ResponseEntity<Object>(userDetailService.verifyEmployeeCodeAndGenerateOtp(loginDto, response), HttpStatus.OK);
		
	}	
	
	
	
   //User login 
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception{		
		UserDetail userDetail = userDetailService.verifyLoginCredential(loginDto,response);	
		//String token =  jwtUtil.createToken(userDetail.getEmployeeCode());
		
		Claims claims = Jwts.claims().setSubject(String.valueOf(userDetail.getUserId()));
		claims.put("id", userDetail.getEmployeeCode());
		claims.put("role", userDetail.getRole().getRoleName());		
		claims.put("phone", userDetail.getPhone());
		String token = jwtUtil.createToken(claims);
		
		 
		response.setHeader("Authorization", "Bearer " + token);    		
	    HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        loginDto.setUserId(userDetail.getUserId());
	    loginDto.setEmailId(userDetail.getEmailId());
	    loginDto.setRole(userDetail.getRole());
	    loginDto.setPassword("");
	    loginDto.setLocation(userDetail.getLocation());
	    loginDto.setWalletAmount(userDetail.getWalletAmount());
	    
        
	    return new ResponseEntity<>(loginDto, headers,HttpStatus.OK);
		
	}
	
	
	// Get the user details by userId
	@GetMapping("/getUserDetailsById")
	public ResponseEntity<Object> getUserDetailsById(@RequestParam Long userId, HttpServletResponse response) throws Exception{		
		return new ResponseEntity<Object>(userDetailService.getUserDetailsById(userId, response), HttpStatus.OK);
		
	}
	
	
	
	// Admin login 
	@PostMapping("/adminLogin")
	public ResponseEntity<Object> adminLogin(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception{		
		UserDetail userDetail = userDetailService.verifyAdminLoginCredential(loginDto,response);	
		//String token =  jwtUtil.createToken(userDetail.getEmployeeCode());
		
		Claims claims = Jwts.claims().setSubject(String.valueOf(userDetail.getUserId()));
		claims.put("id", userDetail.getEmployeeCode());
		claims.put("role", userDetail.getRole().getRoleName());
		claims.put("phone", userDetail.getPhone());
		String token = jwtUtil.createToken(claims);
		 
		response.setHeader("Authorization", "Bearer " + token);    		
	    HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        loginDto.setUserId(userDetail.getUserId());
	    loginDto.setEmailId(userDetail.getEmailId());
	    loginDto.setRole(userDetail.getRole());
	    loginDto.setPassword("");
	    loginDto.setUsername(userDetail.getUserName());
	    loginDto.setLocation(userDetail.getLocation());
	          
	    return new ResponseEntity<>(loginDto, headers,HttpStatus.OK);
		
	}
	
	
	
}