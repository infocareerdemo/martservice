package com.mart.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ai.dto.LoginDto;
import com.ai.dto.UpdatePasswordDto;
import com.ai.dto.UserDto;
import com.ai.entity.Role;
import com.ai.entity.UserDetails;
import com.ai.exception.ApplicationException;
import com.ai.message.EmailNotificationService;
import com.ai.message.SmsNotificationService;
import com.ai.repository.RoleRepository;
import com.ai.repository.UserDetailsRepository;
import com.ai.util.CryptoUtils;
import com.ai.util.FunUtils;

@Service
public class UserDetailsService {
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	RoleRepository roleRepository;


	@Autowired
	CryptoUtils cryptoUtils;
	

	@Autowired
	EmailNotificationService emailNotificationService;
	@Autowired
	SmsNotificationService smsNotificationService;
	
	
	public UserDto saveUser(UserDto userDto) throws Exception {
		
		
		System.out.print("\n <<>>>\t"+userDto.toString());
		
		UserDetails userLogin = userDetailsRepository.findByEmailId(userDto.getEmailId());
		
		if(userLogin!=null) 
		{

		    if (userDto.getPassWord() != null)
		    {
		    	System.out.print("\n <<>>>\t"+userDto.getEmailId());
		    	System.out.print("\n <<>>>\t"+userDto.getPassWord());
	
				String key = cryptoUtils.generateSecretKey(userDto.getEmailId());
				
				String encryptedPassword = cryptoUtils.encrypt(key, userDto.getPassWord());
					
				userLogin.setKey(key);
		    	System.out.print("\n <<>>>\t"+encryptedPassword);

		        userLogin.setPassWord(encryptedPassword);
		    }
	
		    userLogin.setUserName(userDto.getUserName());
		    
		    userLogin.setEmailId(userDto.getEmailId());
		    userLogin.setEmailVerified(userDto.getEmailVerified());
		    
		    userLogin.setPhone(userDto.getPhoneNo());
		    userLogin.setPhoneVerified(userDto.getPhoneVerified());
	
		    userLogin.setAddress(userDto.getAddress());
		    
			Optional<Role> role = roleRepository.findById((long)1);
		    userLogin.setRole(role.get());
		  	
		    userLogin.setUpdatedDateTime(LocalDateTime.now());
		    
		    UserDetails userlogin1 = userDetailsRepository.save(userLogin);
		    
		    System.out.print("\n<<<<->>>>>>"+userlogin1.toString());
		}
		else
		{
			System.out.print("\n<<<<NOT FOUND ->>>>>>"+userDto.toString());
		}
		
	    return userDto;
	}


	
	  public UserDetails authenticateUserAndGenerateToken(LoginDto loginDto) throws Exception
	  {
		  
          System.out.print("\n<<<---------->>> \t "+loginDto.getUsername());
          UserDetails user = userDetailsRepository.findByEmailId(loginDto.getEmailId());
	        
	        if (user != null) {
	        	
	        	 System.out.print(user.toString());
	        	
	        	String pwd = cryptoUtils.decrypt(user.getKey(), user.getPassWord());
	            
	            System.out.print("\n<<<---------->>> \t "+pwd);
	            System.out.print("\n<<<---------->>> \t "+loginDto.getPassword());

	            
	            if (pwd != null && pwd.equals(loginDto.getPassword())) {
	            	
		            System.out.print("\n inside the if ....");

	                return user;
	            } else 
	            {
	                throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid credentials");
	            }
	            
	            
	        } 
	        else
	        {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid credentials");
	        }
	        
	        
	    }


	  
		public UserDetails verifyEmail(String emailId, int otp) throws ApplicationException {
			
			UserDetails userLogin = userDetailsRepository.findByEmailId(emailId);
			if (userLogin != null) {
				if (userLogin.getEmailOtp() == otp) {
					userLogin.setUpdatedDateTime(LocalDateTime.now());
					//userLogin.setEmailVerified(true);;
					userDetailsRepository.save(userLogin);
					return userLogin;
				} else {
					throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Email otp");
				}
			}
			return null;
		}
		
		
		
		
		
		public String generatePhoneOtp(Long phone, Long id) throws ApplicationException {
			
		  		FunUtils funUtils = new FunUtils();
				int otp = funUtils.generateOtp();
				
				Optional<UserDetails> userLogin = userDetailsRepository.findById(id);
				
				System.out.print(userLogin.get());
				
				if(userLogin!=null)
				{
					userLogin.get().setPhone(phone);
					userLogin.get().setPhoneOtp(otp);
					userLogin.get().setUpdatedDateTime(LocalDateTime.now());
					smsNotificationService.sendOtpToMobile(phone, (long) otp);
					userDetailsRepository.save(userLogin.get());
					return "OTP sent";
										
				}
				
				return "Try Again"; 
		}
		
		

		public boolean verifyPhone(Long phone, int otp) throws ApplicationException {
			UserDetails userLogin = userDetailsRepository.findByPhone(phone);
			if (userLogin != null) {
				if (userLogin.getPhoneOtp() == otp) {
					userLogin.setUpdatedDateTime(LocalDateTime.now());
					return true;
				} else {
					throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "Invalid Phone otp");
				}
			}
			return false;
		}



		
		


	  
}
