package com.mart.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.mart.dto.UserDetailDto;
import com.mart.entity.Location;
import com.mart.entity.Role;
import com.mart.entity.UserDetail;
import com.mart.entity.UserList;
import com.mart.exception.ApplicationException;
import com.mart.message.SmsNotificationService;
import com.mart.repository.LocationRepository;
import com.mart.repository.OrderRepository;
import com.mart.repository.RoleRepository;
import com.mart.repository.UserDetailRepository;
import com.mart.repository.UserListRepository;
import com.mart.util.FunUtils;

@Service
public class CompanyAdminService {
	
	    @Autowired
	    private UserListRepository userListRepository;
	    
	    @Autowired
	    RoleRepository roleRepository;
	    
	    @Autowired
		UserDetailRepository userDetailRepository;
		
		@Autowired
		SmsNotificationService smsNotificationService;
		
		@Autowired
		OrderRepository orderRepository;
		
		@Autowired
		LocationRepository locationRepository;
		

		public List<UserList>  getAllUserListt() throws ApplicationException{			
			List<UserList>  userList = 	userListRepository.findAll();
			  return userList;
		}
		
		public List<UserDetail>  getAllUserList() throws ApplicationException{			
			List<UserDetail>  userList = 	userDetailRepository.findAll();
			  return userList;
		}
		


		public String verifyCmpnyAdminAndSendOtp(Long userId) throws ApplicationException{
			
	  		FunUtils funUtils = new FunUtils();
			int otp = funUtils.generateOtp();
			
			if(userId !=null) {
			Optional<UserDetail> userDetail =	userDetailRepository.findById(userId);
				  if(userDetail !=null) {
					Long phone =  userDetail.get().getPhone();
					
					userDetail.get().setPhoneOtp(otp);
					userDetail.get().setUpdatedDateTime(LocalDateTime.now());
					   
					   smsNotificationService.sendOtpToMobile(phone, (long) otp);
					   userDetailRepository.save(userDetail.get());
						return "OTP SENT";

				  }else {
			           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");

				  }
			}else {
	            throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1001, LocalDateTime.now(), "User does not exist");

			}
			
		}


		public boolean verifyCmpnyAdminAndSendOtp(Long userId, Long reqOtp) throws ApplicationException{
			
			Optional<UserDetail> userDetail =	userDetailRepository.findById(userId);
                if(userDetail !=null) {
                	   if(userDetail.get().getPhoneOtp() == reqOtp) {
                		   return true;
                	   }else {
                		   return false;
                	   }
                	
                }else {
			           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");

                }
		}



		public UserDetailDto userActivateAndDeactivate(UserDetailDto userDetailDto)throws ApplicationException {
			UserDetail userDetail =	userDetailRepository.findByEmployeeCode(userDetailDto.getEmployeeCode());
			
			  UserDetailDto userDetailsDto = new UserDetailDto();
			  
                  if(userDetail !=null) {
				  userDetail.setUserActive(userDetailDto.isUserActive());
				  userDetailRepository.save(userDetail);
				  
			
				  
				  userDetailsDto.setUserName(userDetail.getUserName());
				  userDetailsDto.setPhoneNo(userDetail.getPhone());
				  userDetailsDto.setEmailId(userDetail.getEmailId());
				  userDetailsDto.setRole(userDetail.getRole());
				  userDetailsDto.setAddress(userDetail.getAddress());
				  userDetailsDto.setEmployeeCode(userDetail.getEmployeeCode());
				  userDetailsDto.setUserActive(userDetail.isUserActive());

				  
			  }else {
		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");
	  
			  } 
			return userDetailsDto;
		}

		

		public UserDetailDto addNewUser(UserDetailDto userDetailDto) throws ApplicationException {
		    UserDetailDto userDetailsDto = new UserDetailDto();

		    if (userDetailDto != null) {
		        try {
		            UserDetail userDetail = new UserDetail();
		            userDetail.setEmployeeCode(userDetailDto.getEmployeeCode());
		            userDetail.setName(userDetailDto.getName());
		            userDetail.setUserName(userDetailDto.getUserName());
		            userDetail.setPhone(userDetailDto.getPhoneNo());
		            userDetail.setEmailId(userDetailDto.getEmailId());
		            userDetail.setRole(userDetailDto.getRole());
		            userDetail.setUserActive(true);

		            UserDetail savedUserDetail = userDetailRepository.save(userDetail);

		            userDetailsDto.setUserName(savedUserDetail.getUserName());
		            userDetailsDto.setPhoneNo(savedUserDetail.getPhone());
		            userDetailsDto.setUserName(savedUserDetail.getUserName());
		            userDetailsDto.setName(savedUserDetail.getName());
		            userDetailsDto.setEmailId(savedUserDetail.getEmailId());
		            userDetailsDto.setRole(savedUserDetail.getRole());
		            userDetailsDto.setAddress(savedUserDetail.getAddress());
		            userDetailsDto.setEmployeeCode(savedUserDetail.getEmployeeCode());
		            userDetailsDto.setUserActive(savedUserDetail.isUserActive());

		        } catch (DataIntegrityViolationException ex) {
	
		        	   throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
		                       "User with this email or phone already exists");
		            }
		        

		    } else {
		        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data Not Found");
		    }

		    return userDetailsDto;
		}

		
		

		public UserDetailDto getUserDetailsById(Long userId) throws ApplicationException {
			  UserDetailDto userDetailsDto = new UserDetailDto();

			if(userId !=null) {
				
				Optional<UserDetail> userDetails =	userDetailRepository.findById(userId);
				
				if(userDetails !=null) {
					UserDetail userDetail =	userDetails.get();
					
					  userDetailsDto.setUserName(userDetail.getName());
					  userDetailsDto.setPhoneNo(userDetail.getPhone());
					  userDetailsDto.setUserName(userDetail.getUserName());
					  userDetailsDto.setEmailId(userDetail.getEmailId());
					  userDetailsDto.setRole(userDetail.getRole());
					  userDetailsDto.setAddress(userDetail.getAddress());
					  userDetailsDto.setEmployeeCode(userDetail.getEmployeeCode());
					  userDetailsDto.setUserActive(userDetail.isUserActive());
					
				}else {
			           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "user Not Found");

				}
				
			}else {
		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "data Not Found");

			}
			return userDetailsDto;
		}

		public UserDetailDto updateUser(UserDetailDto userDetailDto) throws ApplicationException{
			 UserDetailDto userDetailsDto = new UserDetailDto();
			if(userDetailDto.getUserId() !=null) {
				Optional<UserDetail> userDetails =	userDetailRepository.findById(userDetailDto.getUserId());
                   if(userDetails !=null) {
                	   UserDetail userDetail =	userDetails.get();
                	   userDetail.setName(userDetailDto.getName());
                	   userDetail.setUserName(userDetailDto.getName());
                	   userDetail.setEmailId(userDetailDto.getEmailId());
                	   userDetailRepository.save(userDetail);
                	   

 					  userDetailsDto.setUserName(userDetail.getUserName());
 					  userDetailsDto.setPhoneNo(userDetail.getPhone());
 					  userDetailsDto.setName(userDetail.getName());
 					  userDetailsDto.setEmailId(userDetail.getEmailId());
 					  userDetailsDto.setRole(userDetail.getRole());
 					  userDetailsDto.setAddress(userDetail.getAddress());
 					  userDetailsDto.setEmployeeCode(userDetail.getEmployeeCode());
 					  userDetailsDto.setUserActive(userDetail.isUserActive());
                	   
                   }else {
    		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "user Not Found");

                   }
				
			}else {
		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "id Not Found");

			}
			
			return userDetailsDto;
		}
		
	
		public List<UserDetailDto> saveMultipleUserr(List<UserDetailDto> userDetailListDto) throws ApplicationException {
		    List<UserDetailDto> userDetailsDtoList = new ArrayList<>();

		    if (userDetailListDto != null && !userDetailListDto.isEmpty()) {
		        for (UserDetailDto user : userDetailListDto) {

		            Optional<UserDetail> existingUser = userDetailRepository.findByEmployeeCodeAndPhone(user.getEmployeeCode(), user.getPhoneNo());
		            if (existingUser.isPresent()) {
		                System.out.println("User with EmployeeCode: " + user.getEmployeeCode() + " and Phone: " + user.getPhoneNo() + " already exists.");
		                continue; 
		            }

		            UserDetail userDetail = new UserDetail();
		            userDetail.setEmployeeCode(user.getEmployeeCode());
		            userDetail.setUserName(user.getName());
		            userDetail.setPhone(user.getPhoneNo());
		            userDetail.setEmailId(user.getEmailId());
		            userDetail.setUserActive(true);

		            Optional<Role> optionalRole = roleRepository.findById(2L);
		            if (optionalRole.isPresent()) {
		                userDetail.setRole(optionalRole.get());
		            } else {
		                throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), "Role not found for ID: 2");
		            }

		            Optional<Location> optionalLocation = locationRepository.findById(1L);
		            if (optionalLocation.isPresent()) {
		                userDetail.setLocation(optionalLocation.get());
		            } else {
		                throw new ApplicationException(HttpStatus.NOT_FOUND, 1003, LocalDateTime.now(), "Location not found for ID: 1");
		            }

		            UserDetail savedUserDetail = userDetailRepository.save(userDetail);

		            UserDetailDto userDetailsDto = new UserDetailDto();
		            userDetailsDto.setUserName(savedUserDetail.getUserName());
		            userDetailsDto.setPhoneNo(savedUserDetail.getPhone());
		            userDetailsDto.setUserName(savedUserDetail.getName());
		            userDetailsDto.setEmailId(savedUserDetail.getEmailId());
		            userDetailsDto.setRole(savedUserDetail.getRole());
		            userDetailsDto.setAddress(savedUserDetail.getAddress());
		            userDetailsDto.setEmployeeCode(savedUserDetail.getEmployeeCode());
		            userDetailsDto.setUserActive(savedUserDetail.isUserActive());
		            userDetailsDto.setLocation(savedUserDetail.getLocation());

		            userDetailsDtoList.add(userDetailsDto);
		        }
		    } else {
		        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Users data Not Found");
		    }

		    return userDetailsDtoList;
		}


		
		
		
		

		public List<UserDetailDto> saveMultipleUser(List<UserDetailDto> userDetailDtos) {
		    List<UserDetailDto> duplicateUsers = new ArrayList<>();

		    // Check for duplicates
		    for (UserDetailDto userDto : userDetailDtos) {
		        boolean isDuplicate = userDetailRepository.existsByEmployeeCodeOrPhoneNoOrEmailId(
		                userDto.getEmployeeCode(), userDto.getPhoneNo(), userDto.getEmailId());

		        if (isDuplicate) {
		            // If duplicate, add to the duplicate user list
		            duplicateUsers.add(userDto);
		        }
		    }

		    // If there are any duplicates, return the list of duplicates without saving
		    if (!duplicateUsers.isEmpty()) {
		        return duplicateUsers; // Return duplicates
		    }

		    // If no duplicates, save new users
		    for (UserDetailDto userDto : userDetailDtos) {
		        UserDetail newUser = new UserDetail();
		        newUser.setUserName(userDto.getUserName());
		        newUser.setPassWord(userDto.getPassWord());
		        newUser.setEmailId(userDto.getEmailId());
		        newUser.setPhone(userDto.getPhoneNo());
		        newUser.setAddress(userDto.getAddress());
		        newUser.setEmployeeCode(userDto.getEmployeeCode());
		        newUser.setRole(userDto.getRole());
		        newUser.setLocation(userDto.getLocation());
		        newUser.setUserActive(userDto.isUserActive());

		        // Save the new user in UserDetails repository
		        userDetailRepository.save(newUser);

		        /* Uncomment this if you want to save in UserList repository
		        UserList userListEntry = new UserList();
		        userListEntry.setUserId(newUser.getUserId());
		        userListEntry.setEmail(newUser.getEmailId());
		        userListEntry.setName(newUser.getUserName());
		        userListEntry.setEmployeeCode(newUser.getEmployeeCode());

		        // Save the new user in UserList repository
		        userListRepository.save(userListEntry);
		        */
		    }

		    return new ArrayList<>(); // Return an empty list if no duplicates found
		}

		  
		    

			public List<UserList> addWallet(List<UserList> userList, LocalDateTime futureDate) throws ApplicationException {
			    if (userList != null && !userList.isEmpty()) {
			        for (UserList user : userList) {
			            Optional<UserDetail> existingUser = userDetailRepository
			                   .findByEmployeeCodeAndPhone(user.getEmployeeCode(), user.getPhone());
			            
			            System.out.println("Exis:"+existingUser.get().getEmployeeCode() );
			            System.out.println("Exis:"+existingUser.get().getPhone() );
			            
			            if (existingUser.isPresent()) {
			            			            	
			                LocalDateTime uploadFutureDate = futureDate;
			                UserDetail userDetail = existingUser.get();
			                userDetail.setWalletAmount(user.getWalletAmount());
			                userDetailRepository.save(userDetail);
			                
	
			            } else {
			                
			                continue;
			            }
			        }

			        userListRepository.saveAll(userList);
			    } else {
			        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Users data Not Found");
			    }
			    return userList;
			}

			
			
			public UserDetailDto getWalletDetails(Long userId) throws  ApplicationException{
           	 UserDetailDto userDetailDto = new UserDetailDto();

				Optional<UserDetail> userDetails =	userDetailRepository.findById(userId);
				
                 if(userDetails !=null) {
                	 
                	 userDetailDto.setEmployeeCode(userDetails.get().getEmployeeCode());
                	 userDetailDto.setWalletAmount(userDetails.get().getWalletAmount());
                	 userDetailDto.setPhoneNo(userDetails.get().getPhone());
                	 
                 }else {
  		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "user Not Found");

                 }
            	 return userDetailDto;

			}

	



		
		

}
