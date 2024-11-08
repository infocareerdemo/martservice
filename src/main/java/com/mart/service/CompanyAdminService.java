package com.mart.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mart.dto.UserDetailDto;
import com.mart.entity.Location;
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
		
	    @Value("${scheduler.cron.expression}")
	    private String cronExpression;

	    // Use the cron expression from the application properties
	    @Scheduled(cron = "#{@schedulerCronExpression}")
	    public void updateWalletAmountForFutureDates() {
	        LocalDateTime now = LocalDateTime.now();

	        List<UserList> userList = userListRepository.findByFutureDateTimeLessThanEqual(now);

	        for (UserList user : userList) {
	            Optional<UserDetail> existingUserOpt = userDetailRepository
	                    .findByEmployeeCodeAndPhone(user.getEmployeeCode(), user.getPhone());

	            if (existingUserOpt.isPresent()) {
	                UserDetail userDetail = existingUserOpt.get();
	                userDetail.setWalletAmount(user.getWalletAmount());

	                userDetailRepository.save(userDetail);

	                System.out.println("Updated wallet for user with EmployeeCode: " + user.getEmployeeCode());
	            } else {
	                System.err.println("User with EmployeeCode: " + user.getEmployeeCode() + " not found");
	            }
	        }
	    }
	    
		
		 
		// Scheduler to update wallet amounts for users when futureDateTime is reached
	    //  @Scheduled(cron = "0 * * * * ?") // Runs every minute to check if any user has reached their futureDateTime
		 // @Scheduled(cron = "0 0 1 * * ?") // Runs every day at 1:00 AM
		 // @Scheduled(cron = "0 */5 * * * ?") // Runs every 5 minutes  to check if any user has reached their futureDateTime
		  //@Scheduled(cron = "0 * * * * ?") // Runs every minute to check if any user has reached their futureDateTime
		 /* public void updateWalletAmountForFutureDates() {
		      LocalDateTime now = LocalDateTime.now();
		      
		      List<UserList> userList = userListRepository.findByFutureDateTimeLessThanEqual(now);

		      for (UserList user : userList) {
		          Optional<UserDetail> existingUserOpt = userDetailRepository
		                  .findByEmployeeCodeAndPhone(user.getEmployeeCode(), user.getPhone());

		          if (existingUserOpt.isPresent()) {
		              UserDetail userDetail = existingUserOpt.get();
		              userDetail.setWalletAmount(user.getWalletAmount());

		              userDetailRepository.save(userDetail);

		              System.out.println("Updated wallet for user with EmployeeCode: " + user.getEmployeeCode());
		          } else {
		              System.err.println("User with EmployeeCode: " + user.getEmployeeCode() + " not found");
		          }
		      }
		  }*/
		  
		  
		
		 public List<UserList> addWalletUpdated(List<UserList> userList, LocalDateTime futureDate) throws ApplicationException {
			    if (userList != null && !userList.isEmpty()) {
			        for (UserList user : userList) {
			            Optional<UserDetail> existingUser = userDetailRepository
			                    .findByEmployeeCodeAndPhone(user.getEmployeeCode(), user.getPhone());

			            if (existingUser.isPresent()) {
			                UserDetail userDetail = existingUser.get();
			                
			                UserList newUserList = new UserList();
			                newUserList.setEmployeeCode(user.getEmployeeCode());
			                newUserList.setPhone(user.getPhone());
			                newUserList.setWalletAmount(user.getWalletAmount());  
			                
			                newUserList.setFutureDateTime(futureDate); 
			                newUserList.setUpdatedCurrentDateTime(LocalDateTime.now());

			                userListRepository.save(newUserList);

			            } else {
			                throw new ApplicationException(HttpStatus.NOT_FOUND, 1002, LocalDateTime.now(), 
			                                               "User with EmployeeCode: " + user.getEmployeeCode() + " not found");
			            }
			        }
			        return userList;  
			    } else {
			        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Users data Not Found");
			    }
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
					userDetail.get().setPhoneOtpExpiry(LocalDateTime.now().plusMinutes(3));
					   
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
       	                      if (userDetail.get().getPhoneOtpExpiry() != null && LocalDateTime.now().isBefore(userDetail.get().getPhoneOtpExpiry())) {
       	                         return true;
       	                         } else {
       	                    throw new ApplicationException(HttpStatus.UNAUTHORIZED, 1002, LocalDateTime.now(), "OTP has expired");
       	                }
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
		            userDetail.setPhone(userDetailDto.getPhoneNo());
		            userDetail.setEmailId(userDetailDto.getEmailId());
		            userDetail.setRole(userDetailDto.getRole());
		            userDetail.setUserActive(true);
		            
		    		Optional<Location> location = locationRepository.findById(1L);
		    		userDetail.setLocation(location.get());


		            UserDetail savedUserDetail = userDetailRepository.save(userDetail);

		            userDetailsDto.setPhoneNo(savedUserDetail.getPhone());
		            userDetailsDto.setName(savedUserDetail.getName());
		            userDetailsDto.setEmailId(savedUserDetail.getEmailId());
		            userDetailsDto.setRole(savedUserDetail.getRole());
		            userDetailsDto.setAddress(savedUserDetail.getAddress());
		            userDetailsDto.setEmployeeCode(savedUserDetail.getEmployeeCode());
		            userDetailsDto.setUserActive(savedUserDetail.isUserActive());
		            userDetailsDto.setLocation(savedUserDetail.getLocation());
		            

		        } catch (DataIntegrityViolationException ex) {
	
		        	   throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
		                       "User with this email or phone already exists");
		            }
		        

		    } else {
		        throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Data Not Found");
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
                	   userDetail.setEmailId(userDetailDto.getEmailId());
                	   userDetail.setPhone(userDetailDto.getPhoneNo());
                	   userDetailRepository.save(userDetail);
                	   

                	  userDetailsDto.setUserId(userDetail.getUserId());
 					  userDetailsDto.setPhoneNo(userDetail.getPhone());
 					  userDetailsDto.setName(userDetail.getName());
 					  userDetailsDto.setEmailId(userDetail.getEmailId());
 					  userDetailsDto.setRole(userDetail.getRole());
 					  userDetailsDto.setLocation(userDetail.getLocation());
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
		
		
		public String updateWalletToOneUser(UserList userList, LocalDateTime futureDate) throws ApplicationException{
				 
				 if(userList.getEmployeeCode()  !=null) {
					 
					    Optional<UserDetail> existingUser = userDetailRepository
			                    .findByEmployeeCodeAndPhone(userList.getEmployeeCode(), userList.getPhone());

			            if (existingUser.isPresent()) {
			                UserDetail userDetail = existingUser.get();
			                
			                UserList newUserList = new UserList();
			                newUserList.setEmployeeCode(userList.getEmployeeCode());
			                newUserList.setPhone(userList.getPhone());
			                newUserList.setWalletAmount(userList.getWalletAmount());  
			                
			                newUserList.setFutureDateTime(futureDate); 
			                newUserList.setUpdatedCurrentDateTime(LocalDateTime.now());

			                userListRepository.save(newUserList);
					 
					 
				 }else {
			           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "User Not Found");

				 }
			 }else {
		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "data Not Found");

			 }
			
	    	
		   return "Wallet updated Succesfully";
		
		}
		

		public UserDetailDto getUserDetailsById(Long userId) throws ApplicationException {
			  UserDetailDto userDetailsDto = new UserDetailDto();

			if(userId !=null) {
				
				Optional<UserDetail> userDetails =	userDetailRepository.findById(userId);
				
				if(userDetails !=null) {
					UserDetail userDetail =	userDetails.get();
					
					  userDetailsDto.setUserId(userDetails.get().getUserId());
					  userDetailsDto.setPhoneNo(userDetail.getPhone());
					  userDetailsDto.setName(userDetail.getName());
					  userDetailsDto.setEmailId(userDetail.getEmailId());
					  userDetailsDto.setRole(userDetail.getRole());
					  userDetailsDto.setAddress(userDetail.getAddress());
					  userDetailsDto.setEmployeeCode(userDetail.getEmployeeCode());
					  userDetailsDto.setUserActive(userDetail.isUserActive());
					  userDetailsDto.setWalletAmount(userDetail.getWalletAmount());
					
				}else {
			           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "user Not Found");

				}
				
			}else {
		           throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "data Not Found");

			}
			return userDetailsDto;
		}
		
		

		
		
		
		

		public List<UserDetailDto> saveMultipleUser(List<UserDetailDto> userDetailDtos) {
		    List<UserDetailDto> duplicateUsers = new ArrayList<>();

		    for (UserDetailDto userDto : userDetailDtos) {
		        boolean isDuplicate = userDetailRepository.existsByEmployeeCodeOrPhoneNoOrEmailId(
		                userDto.getEmployeeCode(), userDto.getPhoneNo(), userDto.getEmailId());

		        if (isDuplicate) {
		        	
		            duplicateUsers.add(userDto);
		        }
		    }

		    if (!duplicateUsers.isEmpty()) {
		        return duplicateUsers; 
		    }

		    for (UserDetailDto userDto : userDetailDtos) {
		        UserDetail newUser = new UserDetail();
		        newUser.setName(userDto.getName());
		        newUser.setPassWord(userDto.getPassWord());
		        newUser.setEmailId(userDto.getEmailId());
		        newUser.setPhone(userDto.getPhoneNo());
		        newUser.setAddress(userDto.getAddress());
		        newUser.setEmployeeCode(userDto.getEmployeeCode());
		        newUser.setRole(userDto.getRole());
		        newUser.setUserActive(true);		     
		        
		        Optional<Location> location = locationRepository.findById(1L);
		        newUser.setLocation(location.get());


		        userDetailRepository.save(newUser);
		        
		    }

		    return new ArrayList<>(); 
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

	



			  public ByteArrayInputStream generateEmptyExcelWithHeaders() throws IOException {
			        String[] columns = {"S.no", "employeeCode", "phone", "name", "email"};

			        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			            Sheet sheet = workbook.createSheet("Employee Data");

			            Row headerRow = sheet.createRow(0);
			            for (int i = 0; i < columns.length; i++) {
			                Cell cell = headerRow.createCell(i);
			                cell.setCellValue(columns[i]);
			            }

			            for (int i = 0; i < columns.length; i++) {
			                sheet.autoSizeColumn(i);
			            }

			            workbook.write(out);
			            return new ByteArrayInputStream(out.toByteArray());
			        }
			    }
			  
			  

			  public ByteArrayInputStream generateEmptyExcelForWalletWithHeaders() throws IOException {
			        String[] columns = {"S.no", "employeeCode", "phone", "walletAmount"};

			        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			            Sheet sheet = workbook.createSheet("Wallet Data");

			            Row headerRow = sheet.createRow(0);
			            for (int i = 0; i < columns.length; i++) {
			                Cell cell = headerRow.createCell(i);
			                cell.setCellValue(columns[i]);
			            }

			            for (int i = 0; i < columns.length; i++) {
			                sheet.autoSizeColumn(i);
			            }

			            workbook.write(out);
			            return new ByteArrayInputStream(out.toByteArray());
			        }
			    }
		
			  
			
			  
			  


public ByteArrayInputStream generateWalletDetailsReport(LocalDate currentDate) throws IOException {
    LocalDateTime startOfDay = currentDate.atStartOfDay();
    LocalDateTime endOfDay = currentDate.atTime(LocalTime.MAX);

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String formattedDate = currentDate.format(dateFormatter);

    List<UserList> userList = userListRepository.findByUpdatedCurrentDateTimeBetween(startOfDay, endOfDay);

    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        Sheet sheet = workbook.createSheet("Wallet Details");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12); 
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row dateRow = sheet.createRow(0);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Date : " + formattedDate); 

        CellStyle dateCellStyle = workbook.createCellStyle();
        Font dateFont = workbook.createFont();
        dateFont.setBold(true);
        dateFont.setFontHeightInPoints((short) 14); 
        dateCellStyle.setFont(dateFont);
        dateCell.setCellStyle(dateCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        String[] headers = {"S.No", "Employee Code", "Name", "Opening Balance", "Utilization", "Closing Balance"};
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowIdx = 2; 
        for (int i = 0; i < userList.size(); i++) {
            UserList user = userList.get(i);
            UserDetail userDetails = userDetailRepository.findByEmployeeCode(user.getEmployeeCode());

            double openingBalance = user.getWalletAmount(); 
            double pendingBalance = userDetails.getWalletAmount(); 
            double utilization = openingBalance - pendingBalance; 

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(i + 1); 
            row.createCell(1).setCellValue(user.getEmployeeCode()); 
            row.createCell(2).setCellValue(userDetails.getName()); 
            row.createCell(3).setCellValue(openingBalance); 
            row.createCell(4).setCellValue(utilization); 
            row.createCell(5).setCellValue(pendingBalance); 
        }

        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
    
}
		
			            
}


