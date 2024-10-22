package com.mart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mart.entity.UserDetail;




public interface UserDetailRepository extends JpaRepository<UserDetail, Long>{

	UserDetail findByUserName(String userName);

	UserDetail findByPhone(Long phone);	
	
    UserDetail findByEmployeeCode(String employeeCode);
    
    Optional<UserDetail> findByEmployeeCodeAndPhone(String employeeCode, Long phone);
    Optional<UserDetail> findByEmployeeCodeIgnoreCaseAndPhone(String employeeCode, Long phone);


    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserDetail u " +
    	       "WHERE u.employeeCode = :employeeCode OR u.phone = :phone OR u.emailId = :emailId")
    	boolean existsByEmployeeCodeOrPhoneNoOrEmailId(@Param("employeeCode") String employeeCode, 
    	                                               @Param("phone") Long phone, 
    	                                               @Param("emailId") String emailId);


//	UserDetail findByPhone(Long phone);
	
	//Optional<UserDetail> findById(Long userId);
	
}
