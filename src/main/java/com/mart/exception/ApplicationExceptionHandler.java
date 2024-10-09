package com.mart.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {
	
	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<ApplicationException> globalExceptionHandler(ApplicationException ex){
		return new ResponseEntity<ApplicationException>(ex,ex.getHttpStatus());
	}	
	

}
