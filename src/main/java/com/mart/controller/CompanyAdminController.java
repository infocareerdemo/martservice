package com.mart.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mart.dto.UserDetailDto;
import com.mart.entity.UserList;
import com.mart.service.CompanyAdminService;


@RestController
@RequestMapping("/api/v1/companyadmin")
public class CompanyAdminController {

	@Autowired
	CompanyAdminService  companyAdminService;

	
	// update wallet
	@PostMapping("/addWallet")
	public ResponseEntity<Object> addWalletUpdated(@RequestBody List<UserList> userList, @RequestParam LocalDateTime futureDate ) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.addWalletUpdated(userList,futureDate), HttpStatus.OK);
		
	}
	
	
	// get all userlist 
	@GetMapping("/getAllUserList")
	public ResponseEntity<Object> getAllUserList() throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.getAllUserList(), HttpStatus.OK);
		
	}
	
	//Verify the company admin and send otp for update wallet amount to users
	@GetMapping("/verifyCmpnyAdminAndSendOtp")
	public ResponseEntity<Object> verifyCmpnyAdminAndSendOtp(@RequestParam Long userId) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.verifyCmpnyAdminAndSendOtp(userId), HttpStatus.OK);
		
	}
	
	// Verify company admin otp
	@GetMapping("/verifyOtp")
	public ResponseEntity<Object> verifyOtp(@RequestParam Long userId, @RequestParam Long reqOtp) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.verifyCmpnyAdminAndSendOtp(userId,reqOtp), HttpStatus.OK);
		
	}
	

    // Set user activate and de-activated
	@PostMapping("/userActivateAndDeactivate")
	public ResponseEntity<Object> userActivateAndDeactivate(@RequestBody UserDetailDto userDetailDto) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.userActivateAndDeactivate(userDetailDto), HttpStatus.OK);
		
	}
	
     // Add single user by company admin
	@PostMapping("/addNewUser")
	public ResponseEntity<Object> addNewUser(@RequestBody UserDetailDto userDetailDto) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.addNewUser(userDetailDto), HttpStatus.OK);
		
	}
	
	// Update the user by  company admin
	@PostMapping("/updateUser")
	public ResponseEntity<Object> updateUser(@RequestBody UserDetailDto userDetailDto) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.updateUser(userDetailDto), HttpStatus.OK);
		
	}
	
	//Update the wallet for single user
	@PostMapping("/updateWalletToOneUser")
	public ResponseEntity<Object> updateWalletToOneUser(@RequestBody UserList userList,@RequestParam LocalDateTime futureDate) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.updateWalletToOneUser(userList,futureDate), HttpStatus.OK);
		
	}
	
	//Get User details by userId
	@GetMapping("/getUserDetailsById")
	public ResponseEntity<Object> getUserDetailsById(@RequestParam  Long userId) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.getUserDetailsById(userId), HttpStatus.OK);
		
	}
	

	//Save Multiple user
	@PostMapping("/saveMultipleUser")
	public ResponseEntity<Object> saveMultipleUser(@RequestBody List<UserDetailDto> userList) {
	     List<UserDetailDto> duplicateUsers;

	        try {
	            duplicateUsers = companyAdminService.saveMultipleUser(userList);

	            if (!duplicateUsers.isEmpty()) {
	                return new ResponseEntity<>(duplicateUsers, HttpStatus.CONFLICT);
	            }

	            return new ResponseEntity<>("All users saved successfully", HttpStatus.OK);
	        } catch (Exception e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	
	
	
	// Get Wallet details by userId
	@GetMapping("/getWalletDetails")
	public ResponseEntity<Object> getWalletDetails(@RequestParam Long userId) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.getWalletDetails(userId), HttpStatus.OK);
		
	}
	
	
	// Download Multiple user excel template
	@PostMapping("/downloadMultipleUserExcel")
    public ResponseEntity<InputStreamResource> downloadMultipleUserExcel() throws IOException {
        ByteArrayInputStream excelFile = companyAdminService.generateEmptyExcelWithHeaders();

	    String filename = "multiuser_upload_template.xlsx";
	    
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelFile));
    }
	
    
	// Download Wallet excel template
	@PostMapping("/downloadWalletUploadExcel")
	public ResponseEntity<InputStreamResource> downloadWalletUploadExcel() throws IOException {
	    ByteArrayInputStream excelFile = companyAdminService.generateEmptyExcelForWalletWithHeaders();

	    String filename = "wallet_upload_template.xlsx";

	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Disposition", "attachment; filename=" + filename);

	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	            .body(new InputStreamResource(excelFile));
	}
	
	
    // Download Wallet details report for specific date
    @PostMapping("/downloadWalletDetailsReport")
    public ResponseEntity<InputStreamResource> downloadWalletDetailsReport(
    		   @RequestParam(required = false) LocalDate currentDate) throws IOException {

        ByteArrayInputStream excelFile = companyAdminService.generateWalletDetailsReport(currentDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=wallet_details_report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelFile));
    }

}
