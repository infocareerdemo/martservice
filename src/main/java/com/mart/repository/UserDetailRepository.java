package com.mart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.UserDetail;




public interface UserDetailRepository extends JpaRepository<UserDetail, Long>{

	UserDetail findByUserName(String userName);

	UserDetail findByPhone(Long phone);	
	
    UserDetail findByEmployeeCode(String employeeCode);
    
    Optional<UserDetail> findByEmployeeCodeAndPhone(String employeeCode, Long phone);
    Optional<UserDetail> findByEmployeeCodeIgnoreCaseAndPhone(String employeeCode, Long phone);


//	UserDetail findByPhone(Long phone);
	
	//Optional<UserDetail> findById(Long userId);
	
}
