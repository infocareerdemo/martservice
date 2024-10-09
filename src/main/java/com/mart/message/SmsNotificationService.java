package com.mart.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsNotificationService {
	
	@Autowired
	Environment env;
	
	@Autowired
	RestTemplate restTemplate;

	public void sendOtpToMobile(Long mobileNo, Long otp) {
		String routeNo = env.getProperty("sms_route");
		String key = env.getProperty("sms_access_token");
		String sender = env.getProperty("sms_sender");
		String templateId = env.getProperty("sms_template_id");
		String content = env.getProperty("sms_content");
		
		String modifiedContent = content.replace("[otp]", String.valueOf(otp));
		
		restTemplate.getForEntity("https://api.co3.live/api/smsapi?key=" + key +"&route="+ routeNo + "&sender=" + sender + "&number=" + mobileNo +"&templateid=" + templateId +"&sms=" + modifiedContent, null);
	}
}
