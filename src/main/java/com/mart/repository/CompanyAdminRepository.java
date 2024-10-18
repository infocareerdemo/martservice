package com.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.UserDetail;

public interface CompanyAdminRepository extends JpaRepository<UserDetail, Long>{

	
}
