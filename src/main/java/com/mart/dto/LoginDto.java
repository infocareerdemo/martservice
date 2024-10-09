package com.mart.dto;


import com.mart.entity.Role;

import lombok.Data;

@Data
public class LoginDto {

	private Long userId;
	
	private String username;
	
	private String emailId;
	
	private Long phone;
	
	private Long phoneOTP;

	
	private String password;
	
	private Role role;
}
