package com.mart.controller;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.dto.OrderStatusDto;
import com.mart.dto.UserDetailDto;
import com.mart.entity.UserList;
import com.mart.service.CompanyAdminService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/companyadmin")
public class CompanyAdminController {

	@Autowired
	CompanyAdminService  companyAdminService;
	
	
	@PostMapping("/saveUserList")
	public ResponseEntity<Object> saveUserList(@RequestBody List<UserList> userList, @RequestParam  LocalDate futureDate) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.saveUserList(userList,futureDate), HttpStatus.OK);
		
	}
	
	
	@GetMapping("/getAllUserList")
	public ResponseEntity<Object> getAllUserList() throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.getAllUserList(), HttpStatus.OK);
		
	}
	
	@GetMapping("/verifyCmpnyAdminAndSendOtp")
	public ResponseEntity<Object> verifyCmpnyAdminAndSendOtp(@RequestParam Long userId) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.verifyCmpnyAdminAndSendOtp(userId), HttpStatus.OK);
		
	}
	
	
	@GetMapping("/verifyOtp")
	public ResponseEntity<Object> verifyOtp(@RequestParam Long userId, @RequestParam Long reqOtp) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.verifyCmpnyAdminAndSendOtp(userId,reqOtp), HttpStatus.OK);
		
	}
	


	@PostMapping("/userActivateAndDeactivate")
	public ResponseEntity<Object> userActivateAndDeactivate(@RequestBody UserDetailDto userDetailDto) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.userActivateAndDeactivate(userDetailDto), HttpStatus.OK);
		
	}
	

	@PostMapping("/addNewUser")
	public ResponseEntity<Object> addNewUser(@RequestBody UserDetailDto userDetailDto) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.addNewUser(userDetailDto), HttpStatus.OK);
		
	}
	
	@PostMapping("/updateUser")
	public ResponseEntity<Object> updateUser(@RequestBody UserDetailDto userDetailDto) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.updateUser(userDetailDto), HttpStatus.OK);
		
	}
	
	@GetMapping("/getUserDetailsById")
	public ResponseEntity<Object> getUserDetailsById(@RequestParam  Long userId) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.getUserDetailsById(userId), HttpStatus.OK);
		
	}
	

	@PostMapping("/saveMultipleUser")
	public ResponseEntity<Object> saveMultipleUser(@RequestBody List<UserDetailDto> userList) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.saveMultipleUser(userList), HttpStatus.OK);
		
	}
	

}
