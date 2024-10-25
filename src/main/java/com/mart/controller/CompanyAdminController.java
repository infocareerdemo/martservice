package com.mart.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	/*@PostMapping("/addWallet")
	public ResponseEntity<Object> addWallet(@RequestBody List<UserList> userList, @RequestParam  LocalDateTime futureDate) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.addWallet(userList,futureDate), HttpStatus.OK);
		
	}*/
	
	@PostMapping("/addWallet")
	public ResponseEntity<Object> addWalletUpdated(@RequestBody List<UserList> userList) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.addWalletUpdated(userList), HttpStatus.OK);
		
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
	
	@PostMapping("/updateWalletToOneUser")
	public ResponseEntity<Object> updateWalletToOneUser(@RequestBody UserDetailDto userDetailDto) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.updateWalletToOneUser(userDetailDto), HttpStatus.OK);
		
	}
	
	@GetMapping("/getUserDetailsById")
	public ResponseEntity<Object> getUserDetailsById(@RequestParam  Long userId) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.getUserDetailsById(userId), HttpStatus.OK);
		
	}
	

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
	
	
	@GetMapping("/getWalletDetails")
	public ResponseEntity<Object> getWalletDetails(@RequestParam Long userId) throws Exception{		
		return new ResponseEntity<Object>(companyAdminService.getWalletDetails(userId), HttpStatus.OK);
		
	}
	
	
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


}
