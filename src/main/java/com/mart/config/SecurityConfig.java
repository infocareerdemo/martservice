package com.mart.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Security Config");

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(req -> req
                .requestMatchers(
                    "/api/v1/user/verifyUserName",
                    "/api/v1/user/getotpToPhone",
                    "/api/v1/user/login",
                    "/api/v1/user/adminLogin",                   
                    "/api/v1/user/verifyEmployeeCodeAndGenerateOtp" ,
                    "/api/v1/userlist/upload",
                    "/api/v1/userlist/moveUsers",
                    "/api/v1/product/getAllproductsByCategoryId",
                    "/api/v1/product/getAllCategoriesByProductId"
   
                )
                .permitAll()
                .anyRequest().authenticated())
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .headers(headers -> headers
                .addHeaderWriter((request, response) -> {
                    response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type, Custom-Header");
                }))
            .addFilterBefore(new JwtAuthentication(), BasicAuthenticationFilter.class);

        return http.build();
    }
    
    

    @Bean
     public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000","https://kcsmart.infocareerindia.com")); 
      configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
       configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept")); 
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type")); 
        configuration.setAllowCredentials(true); 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
   }
    
    


}
