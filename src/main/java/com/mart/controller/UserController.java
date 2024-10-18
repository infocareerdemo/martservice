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

import com.mart.config.JwtUtil;
import com.mart.dto.LoginDto;
import com.mart.entity.UserDetail;
import com.mart.exception.ApplicationException;
import com.mart.service.UserDetailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
	
	@Autowired
	UserDetailService userDetailService;
	
	@Autowired
	JwtUtil jwtUtil;
	

	
	@PostMapping("/verifyUserName")
	public ResponseEntity<Object> verifyUserName(@RequestBody LoginDto loginDto, HttpServletResponse response) throws ApplicationException {

		Boolean isValidateUserName = userDetailService.verifyUserName(loginDto);	
		return new ResponseEntity<Object>(isValidateUserName, HttpStatus.OK);
	}
	
	@PostMapping("/getotpToPhone")
	public ResponseEntity<Object> generatePhoneOtp(@RequestBody LoginDto loginDto) throws ApplicationException {
		return new ResponseEntity<Object>(userDetailService.generatePhoneOtp(loginDto.getPhone(), loginDto.getUserId()), HttpStatus.OK);
	}
	
	
	@PostMapping("/loginn")
	public ResponseEntity<Object> loginn(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception {
	  
		UserDetail userDetail = userDetailService.verifyLoginUserDetail(loginDto);	
		
		String token =  jwtUtil.createToken(userDetail.getUserName());
		 
		response.setHeader("Authorization", "Bearer " + token);    		
	    HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
      
  	    loginDto.setUserId(userDetail.getUserId());
	    loginDto.setEmailId(userDetail.getEmailId());
	    loginDto.setRole(userDetail.getRole());
	    loginDto.setPassword("");
	    
	    return new ResponseEntity<>(loginDto, headers,HttpStatus.OK);
	}

	
	/*@PostMapping("/verifyUsernameAndGenerateOtp")
	public ResponseEntity<Object> verifyUsernameAndGenerateOtp(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception{		
		return new ResponseEntity<Object>(userDetailService.verifyUsernameAndGenerateOtp(loginDto, response), HttpStatus.OK);
		
	}*/
	@PostMapping("/verifyEmployeeCodeAndGenerateOtp")
	public ResponseEntity<Object> verifyEmployeeCodeAndGenerateOtp(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception{		
		return new ResponseEntity<Object>(userDetailService.verifyEmployeeCodeAndGenerateOtp(loginDto, response), HttpStatus.OK);
		
	}
	
	
	

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception{		
		UserDetail userDetail = userDetailService.verifyLoginCredential(loginDto,response);	
		String token =  jwtUtil.createToken(userDetail.getUserName());
		 
		response.setHeader("Authorization", "Bearer " + token);    		
	    HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        loginDto.setUserId(userDetail.getUserId());
	    loginDto.setEmailId(userDetail.getEmailId());
	    loginDto.setRole(userDetail.getRole());
	    loginDto.setPassword("");
	    loginDto.setLocation(userDetail.getLocation());
	    
        
	    return new ResponseEntity<>(loginDto, headers,HttpStatus.OK);
		
	}
	
	
	@GetMapping("/getUserDetailsById")
	public ResponseEntity<Object> getUserDetailsById(@RequestParam Long userId, HttpServletResponse response) throws Exception{		
		return new ResponseEntity<Object>(userDetailService.getUserDetailsById(userId, response), HttpStatus.OK);
		
	}
	
	
	
	@PostMapping("/adminLogin")
	public ResponseEntity<Object> adminLogin(@RequestBody LoginDto loginDto, HttpServletResponse response) throws Exception{		
		UserDetail userDetail = userDetailService.verifyAdminLoginCredential(loginDto,response);	
		String token =  jwtUtil.createToken(userDetail.getUserName());
		 
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