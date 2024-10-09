package com.mart.controller;

import java.util.Map;

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

import com.ai.config.JwtUtil;
import com.ai.dto.LoginDto;
import com.ai.dto.UpdatePasswordDto;
import com.ai.dto.UserDto;
import com.ai.entity.UserDetails;
import com.ai.exception.ApplicationException;
import com.ai.service.UserDetailsService;
import com.ai.service.UserLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	@Autowired
	UserDetailsService userDetailService;
	
	@Autowired
	JwtUtil jwtUtil;
	

	
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception {
	  
		UserDetails user = userService.authenticateUserAndGenerateToken(loginDto);	
		
		String token =  jwtUtil.createToken(user.getEmailId());
		 
		response.setHeader("Authorization", "Bearer " + token);    		
	    HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
      
	    String headerToken = response.getHeader("Authorization");
	    System.out.println("Token in Response Header: " + headerToken);	   
	    
	    loginDto.setUserId(user.getUserId());
	    loginDto.setEmailId(user.getEmailId());
	    loginDto.setRole(user.getRole());
	    loginDto.setPassword("");
	    
	    return new ResponseEntity<>(loginDto, headers,HttpStatus.OK);
	}

	
	
	@PostMapping("/logout")
	public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {
	    // Retrieve the token from the Authorization header
	    String token = request.getHeader("Authorization");

	    if (token != null && token.startsWith("Bearer ")) {
	        // Extract the actual token (remove the "Bearer " prefix)
	        String jwtToken = token.substring(7);
	        response.setHeader("Authorization", null);

	        // Send a success response
	        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("Authorization token is missing or invalid", HttpStatus.BAD_REQUEST);
	    }
	}
	
	
	@GetMapping("/otpToPhone")
	public ResponseEntity<Object> generatePhoneOtp(@RequestParam Long phone,@RequestParam(required = false) Long id) throws ApplicationException {
		return new ResponseEntity<Object>(userService.generatePhoneOtp(phone, id), HttpStatus.OK);
	}
	
	@GetMapping("/verifyPhone")
	public ResponseEntity<Object> verifyPhone(@RequestParam Long phone, @RequestParam int otp) throws ApplicationException {
		return new ResponseEntity<Object>(userService.verifyPhone(phone, otp), HttpStatus.OK);
	}
	
	
	
}