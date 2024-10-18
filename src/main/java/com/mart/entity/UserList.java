package com.mart.entity;

import java.time.LocalDate;
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
@Table(name="userlist")
public class UserList {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "userlist_id")
	    private Long userListId;
	 
	    @Column(name = "employee_code")
	    private String employeeCode;

	    @Column(name = "name")
	    private String name;

	    @Column(name = "phone")
	    private Long phone; 
	    
	    @Column(name = "email")
	    private String email; 

	    @Column(name = "wallet_amount")
	    private double walletAmount; 
	
	    @Column(name = "\"current_date\"")  // Escaping current_date to avoid PostgreSQL conflict
		private LocalDate currentDate;

	    @Column(name = "future_date")
	    private LocalDate futureDate;
	    

		@Column(name = "updated_date_time")
		private LocalDateTime updatedDateTime;

	
}
