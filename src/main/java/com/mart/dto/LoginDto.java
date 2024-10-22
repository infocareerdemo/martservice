package com.mart.dto;


import com.mart.entity.Location;
import com.mart.entity.Role;

import lombok.Data;

@Data
public class LoginDto {

	private Long userId;
	
	private String username;
	
	private String emailId;
	
	private Long phone;
	
	private int phoneOTP;

	private Location location;
	
	private String password;
	
	private double walletAmount;
	
	private Role role;
	
	private String employeeCode;
}
