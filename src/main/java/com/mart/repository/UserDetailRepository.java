package com.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.UserDetail;




public interface UserDetailRepository extends JpaRepository<UserDetail, Long>{

	UserDetail findByUserName(String userName);	

//	UserDetail findByPhone(Long phone);
	
	//Optional<UserDetail> findById(Long userId);
	
}
