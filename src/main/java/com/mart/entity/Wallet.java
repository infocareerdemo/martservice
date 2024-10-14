package com.mart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "wallet")
public class Wallet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private Long walletId;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "wallet_user",referencedColumnName = "user_id", nullable = false)
	private UserDetail userDetail;
	
	@Column(name = "wallet_amount")
	private double walletAmount;
	
	@Column(name = "updated_date_time", nullable = false)
	private LocalDateTime updatedDateTime;

}