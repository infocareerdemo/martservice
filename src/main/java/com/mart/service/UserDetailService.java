package com.mart.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
	
	
	  public UserDetail  verifyLoginUserDetail(LoginDto loginDto) throws ApplicationException
	  {
		  
        UserDetail userDetail = userDetailRepository.findByUserName(loginDto.getUsername());
	        
	        if (userDetail != null && userDetail.getUserName()==loginDto.getUsername() && userDetail.getPhoneOtp()==loginDto.getPhoneOTP()) {
	        	
		        return userDetail;

	        	       
	        } 
	        else
	        {
	 	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid credentials");
	        }
	  
	    }
	  
	  
	  public Boolean verifyUserName(LoginDto loginDto)
	  {
		  
        UserDetail userDetail = userDetailRepository.findByUserName(loginDto.getUsername());
	        
	        if (userDetail != null) {
	        	
	        	return true;
	        	            
	        } 
	        else
	        {
	        	return false;
	        }
	  
	        
	    }

	 

	  public String generatePhoneOtp(Long phone, Long id) throws ApplicationException {
			
	  		FunUtils funUtils = new FunUtils();
			int otp = funUtils.generateOtp();
			
			Optional<UserDetail> userDetail = userDetailRepository.findById(id);
			
			
			if(userDetail!=null)
			{
				userDetail.get().setPhoneOtp(otp);
				userDetail.get().setUpdatedDateTime(LocalDateTime.now());
				
				smsNotificationService.sendOtpToMobile(phone, (long) otp);
				userDetailRepository.save(userDetail.get());
				
				return String.valueOf(otp);
									
			}
		    else
	        {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Please try again");
	        }
	}


	/*public boolean  verifyUsernameAndGenerateOtp(LoginDto loginDto, HttpServletResponse response) throws Exception{
		FunUtils funUtils = new FunUtils();
		int otp = funUtils.generateOtp();
		
	  UserDetail userDetails = userDetailRepository.findByUserName(loginDto.getUsername());
	   if(userDetails !=null) {
		   Long phone = userDetails.getPhone();

		   userDetails.setPhoneOtp(otp);
		   userDetails.setUpdatedDateTime(LocalDateTime.now());
		   
		   smsNotificationService.sendOtpToMobile(phone, (long) otp);
		   userDetailRepository.save(userDetails);
		  
	   }else {
           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");

	   }
		
		return true;
	}*/

	public boolean verifyEmployeeCodeAndGenerateOtp(LoginDto loginDto,
			HttpServletResponse response) throws ApplicationException{
		
		FunUtils funUtils = new FunUtils();
		int otp = funUtils.generateOtp();
		
		
		UserDetail userDetails =	userDetailRepository.findByEmployeeCode(loginDto.getEmployeeCode());
		if(userDetails !=null) {
			   Long phone = userDetails.getPhone();

			   userDetails.setPhoneOtp(otp);
			   userDetails.setUpdatedDateTime(LocalDateTime.now());
			   
			   smsNotificationService.sendOtpToMobile(phone, (long) otp);
			   userDetailRepository.save(userDetails);
		}else {
	           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");
		}
		return true;
	}	
	  
	

	public UserDetail verifyLoginCredentiall(LoginDto loginDto, HttpServletResponse response) throws Exception{		
	 UserDetail userDetail = userDetailRepository.findByEmployeeCode(loginDto.getEmployeeCode());
	    if(userDetail !=null) {	    	
	         if(userDetail.getPhoneOtp() == loginDto.getPhoneOTP()) {	        	 
	         }else {
		            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Credentials");
	         }	    	
	    	
	    }else {
	        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");
	    }
		return userDetail;
	}

	public UserDetail verifyLoginCredential(LoginDto loginDto, HttpServletResponse response) throws Exception{		
		 UserDetail userDetail = userDetailRepository.findByEmployeeCode(loginDto.getEmployeeCode());
		    if(userDetail !=null) {
		    	
		    	  if(userDetail.isUserActive() == true) {
		    		  
		    		  if(userDetail.getPhoneOtp() == loginDto.getPhoneOTP()) {	        	 
		 	         }else {
		 		            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Credentials");
		 	         }	
		    		  
		    	  }else {
	 		            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "User is Deactivated");

		    	  }
		    	
		    }else{
		        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");

		    }
		 
		 
			return userDetail;
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
