package com.mart.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler { 
	 
	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<ApplicationException> globalExceptionHandler(ApplicationException ex){
		return new ResponseEntity<ApplicationException>(ex,ex.getHttpStatus());
	}
	
	
	  /*@ExceptionHandler(ApplicationException.class)
	    public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException ex) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("errorCode", ex.getErrorCode());
	        errorResponse.put("message", ex.getMessage());
	        errorResponse.put("httpStatus", ex.getHttpStatus().value());
	        errorResponse.put("timestamp", ex.getTimestamp());

	        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("errorCode", 500); 
	        errorResponse.put("message", "An unexpected error occurred.");
	        errorResponse.put("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
	        errorResponse.put("timestamp", System.currentTimeMillis());

	        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	    }*/
	    
	

}
