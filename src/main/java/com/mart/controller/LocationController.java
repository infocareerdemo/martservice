package com.mart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mart.entity.Location;
import com.mart.service.LocationService;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

	@Autowired
	LocationService locationService;
	
	
	// Save or Update  Location
	@PostMapping("/saveOrupdateLocation")
	public ResponseEntity<Object> saveOrupdateLocation(@RequestBody Location locationReq)throws Exception{
		return new ResponseEntity<Object>(locationService.saveOrupdateLocation(locationReq), HttpStatus.OK);
		
	}
	
	//Get All Location
	@GetMapping("/getAllLocation")
	public ResponseEntity<Object> getAllLocation() {
		return new ResponseEntity<Object>(locationService.getAllLocation(), HttpStatus.OK);
	}
	
}
