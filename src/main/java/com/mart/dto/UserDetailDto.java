package com.mart.dto;


import com.mart.entity.Role;

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

}
