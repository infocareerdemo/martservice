package com.mart.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity	
@Table(name = "user_details") 
public class UserDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id") 
	private Long userId;

	@Column(name = "user_name", length = 100 ,  unique = true, nullable = true)         
	private String userName;
	
	@Column(name = "employee_code", unique = true)
	private String employeeCode;

	@Column(name = "name", length = 100 , nullable = true)         
	private String name;
	
	@Column(name = "password")
	private String passWord;

	@Column(name = "phone", length = 15, unique = true, nullable = true)
	private Long phone;

	@Column(name = "phone_otp" , nullable = true)
	private int phoneOtp;

	@Column(name = "phone_otp_expiry", nullable = true)
	private LocalDateTime phoneOtpExpiry;
	
	@Column(name = "phone_verified", nullable = true)
	private boolean phoneVerified;

	@Column(name = "email_id", unique = true)
	private String emailId;

	@Column(name = "email_otp" , nullable = true)
	private int emailOtp;
	
	@Column(name = "email_otp_expiry", nullable = true)
	private LocalDateTime emailOtpExpiry;	
	
	@Column(name = "email_verified", nullable = true)
	@ColumnDefault("false")
	private boolean emailVerified;

	@Column(name = "address" , nullable = true)
	private String address;

	@ManyToOne
	@JoinColumn(name = "user_role", referencedColumnName = "role_id")
	private Role role;
	
	@Column(name = "key" , nullable = true)
	private String key;
	
	@Column(name = "consent_chk_flag", nullable = true)
	@ColumnDefault("false")
	private boolean consentChkFlag;

	@Column(name = "updated_date_time")
	private LocalDateTime updatedDateTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_location", referencedColumnName = "location_id" , nullable = true)
	private Location location;
	
	@Column(name = "user_active",nullable = true)
	private boolean userActive;
	
	@Column(name = "wallet_amount" ,nullable = true)
	private double walletAmount;
}
