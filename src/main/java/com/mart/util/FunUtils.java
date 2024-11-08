package com.mart.util;

import java.util.Random;

public class FunUtils {

	
	
	public int generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return Integer.parseInt(String.format("%06d", otp));
	}	
	
		
	
}
