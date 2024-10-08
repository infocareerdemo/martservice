package com.mart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "roles") // Specify the table name
public class Role {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id") // Specify the column name for the primary key
	private Long roleId; 
	
	@Column(name = "role_name", nullable = false)
	private String roleName;
}


/*
 * insert into role( role_id, role_name) values(1,'ADMIN');
 * insert into role( role_id, role_name) values(2,'USER');
 * 
 * 
 */
