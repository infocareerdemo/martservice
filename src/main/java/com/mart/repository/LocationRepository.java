package com.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mart.entity.Location;


public interface LocationRepository extends JpaRepository<Location, Long>{

	Location findByLocationNameAndCompanyName(String locationName, String companyName);

	
}
