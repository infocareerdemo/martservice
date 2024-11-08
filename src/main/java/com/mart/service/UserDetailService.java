package com.mart.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mart.dto.LoginDto;
import com.mart.dto.UserDetailDto;
import com.mart.entity.UserDetail;
import com.mart.exception.ApplicationException;
import com.mart.message.EmailNotificationService;
import com.mart.message.SmsNotificationService;
import com.mart.repository.RoleRepository;
import com.mart.repository.UserDetailRepository;
import com.mart.util.CryptoUtils;
import com.mart.util.FunUtils;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserDetailService {
	
	@Autowired
	UserDetailRepository userDetailRepository;
	
	@Autowired
	RoleRepository roleRepository;

	
	@Autowired
	EmailNotificationService emailNotificationService;
	
	@Autowired
	SmsNotificationService smsNotificationService;
	
	@Autowired
	CryptoUtils cryptoUtils;
	

	public boolean verifyEmployeeCodeAndGenerateOtp(LoginDto loginDto,
			HttpServletResponse response) throws ApplicationException{
		
		FunUtils funUtils = new FunUtils();
		int otp = funUtils.generateOtp();
		
		
		UserDetail userDetails =	userDetailRepository.findByEmployeeCode(loginDto.getEmployeeCode());
		if(userDetails !=null) {
			   Long phone = userDetails.getPhone();

			   userDetails.setPhoneOtp(otp);
			   userDetails.setUpdatedDateTime(LocalDateTime.now());
			   
		        userDetails.setPhoneOtpExpiry(LocalDateTime.now().plusMinutes(3));

			   
			   smsNotificationService.sendOtpToMobile(phone, (long) otp);
			   userDetailRepository.save(userDetails);
		}else {
	           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");
		}
		return true;
	}	
	

	public UserDetail verifyLoginCredential(LoginDto loginDto, HttpServletResponse response) throws Exception {		
	    UserDetail userDetail = userDetailRepository.findByEmployeeCode(loginDto.getEmployeeCode());
	    
	    if (userDetail != null) {
	        if (userDetail.isUserActive()) {
	            
	            if (userDetail.getPhoneOtp() == loginDto.getPhoneOTP()) {
	                
	                if (userDetail.getPhoneOtpExpiry() != null && LocalDateTime.now().isBefore(userDetail.getPhoneOtpExpiry())) {
	                   
	                	return userDetail;
	                } else {
	                    throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1002, LocalDateTime.now(), "OTP has expired");
	                }
	                
	            } else {
	                throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid OTP");
	            }
	            
	        } else {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1003, LocalDateTime.now(), "User is deactivated");
	        }
	        
	    } else {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1004, LocalDateTime.now(), "User not found");
	    }
	}


	public UserDetailDto getUserDetailsById(Long userId, HttpServletResponse response)throws ApplicationException {
		 if(userId !=null) {
			 Optional<UserDetail> userDetail = userDetailRepository.findById(userId);
			   if(userDetail !=null) {
				   UserDetailDto userDetailDto = new UserDetailDto();
				   userDetailDto.setUserName(userDetail.get().getUserName());
				   userDetailDto.setPhoneNo(userDetail.get().getPhone());
				   userDetailDto.setEmailId(userDetail.get().getEmailId());
				   userDetailDto.setRole(userDetail.get().getRole());
				   userDetailDto.setAddress(userDetail.get().getAddress());
				   userDetailDto.setEmployeeCode(userDetail.get().getEmployeeCode());
				   return userDetailDto;				   
			   }else {
			        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");

			   }			 
		 }else{
		        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data Not Found");
		 }
		 
	}



	public UserDetail verifyAdminLoginCredential(LoginDto loginDto, HttpServletResponse response)throws Exception {
		 UserDetail userDetail = userDetailRepository.findByPhone(loginDto.getPhone());	 
		 if(userDetail !=null) {
				String pwd = userDetail.getPassWord();

			 if(pwd.equals(loginDto.getPassword())) {
				 
			 }else {
		            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Credentials");

			 }
		
		 }
			return userDetail;

		
	}


	public UserDetail verifyLoginUserDetail(String employeeCode, Long phoneOTP) {
		 UserDetail userDetail = userDetailRepository.findByEmployeeCode(employeeCode);
		    if(userDetail !=null) {	    	
		         if(userDetail.getPhoneOtp() == phoneOTP) {
		        	 
		         }else {
			            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Credentials");
		         }	    	
		    	
		    }else {
		        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");
		    }
			return userDetail;
	}




}
