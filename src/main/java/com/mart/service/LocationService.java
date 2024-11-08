package com.mart.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mart.entity.Location;
import com.mart.exception.ApplicationException;
import com.mart.repository.LocationRepository;

@Service
public class LocationService {


	@Autowired
	LocationRepository locationRepository;
	

	public  Location saveOrupdateLocation(Location locationReq) throws Exception{
	 Location locationIsExist = locationRepository.findByLocationNameAndCompanyName(locationReq.getLocationName(),locationReq.getCompanyName());
		if(locationIsExist != null) {
				throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(), "Company name already saved by this location");
		}
	   Location location = new Location();
		if(locationReq.getLocationId() == null) {
			location.setLastUpdatedBy(locationReq.getLastUpdatedBy());
			location.setLocationId(locationReq.getLocationId());
			location.setLocationName(locationReq.getLocationName());
			location.setCompanyName(locationReq.getCompanyName());
			location.setLastUpdatedDt(LocalDateTime.now());
			locationRepository.save(location);
			
		}else {
		  Optional<Location> existingLocation = locationRepository.findById(locationReq.getLocationId());
			if(existingLocation.isPresent()) {
				location.setLastUpdatedBy(locationReq.getLastUpdatedBy());
				location.setLocationId(locationReq.getLocationId());
				location.setLocationName(locationReq.getLocationName());
				location.setCompanyName(locationReq.getCompanyName());
				location.setLastUpdatedDt(LocalDateTime.now());
				locationRepository.save(location);
			}
		}
		
		return location;
	}

	public List<Location> getAllLocation() {
	 List<Location> allLocation = 	locationRepository.findAll();
		return allLocation;
	}

}
