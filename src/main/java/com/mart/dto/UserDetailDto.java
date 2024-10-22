package com.mart.dto;


import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.mart.entity.Location;
import com.mart.entity.Role;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class UserDetailDto {
	
		private Long userId;
	    private String userName;
	    private String passWord;
	    private String emailId;
	    private Boolean emailVerified;
	    private Long phoneNo;
	    private Boolean phoneVerified;
	    private String address;
	    private Role role;
	    private String employeeCode;
	    
	    private String name;
	    
	    private double walletAmount;
	
		private Location location;

		private boolean userActive;
	    
	  //  private boolean userDuplicate;


}
