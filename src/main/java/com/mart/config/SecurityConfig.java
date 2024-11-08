package com.mart.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable())
	        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS with a custom config
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/api/v1/user/login",
	                "/api/v1/user/adminLogin",
	                "/api/v1/user/verifyEmployeeCodeAndGenerateOtp"
	            ).permitAll()
	            .anyRequest().authenticated())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Add JwtAuthentication before UsernamePasswordAuthentication

	    return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.addAllowedOrigin("*"); // Adjust based on your frontend origin
	    configuration.addAllowedMethod("*");
	    configuration.addAllowedHeader("*");
	    configuration.setExposedHeaders(List.of("*")); // Expose Authorization header
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}

	 @Bean
   public JwtAuthentication jwtAuthenticationFilter() {
       return new JwtAuthentication();
   }
	 
	 
	 // Bean to get the cron expression from application properties
	    @Bean
	    public String schedulerCronExpression(@Value("${scheduler.cron.expression}") String cronExpression) {
	        return cronExpression;
	    }
	 

}
