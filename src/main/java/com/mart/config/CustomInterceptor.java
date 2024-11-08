package com.mart.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomInterceptor implements HandlerInterceptor {

	@Autowired
	JwtUtil jwtUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
		String token = extractTokenFromRequest(request);
		String bearer = "Bearer ";

		if (token != null) {
			if (!jwtUtil.isTokenExpired(token)) {
				Claims claims = jwtUtil.getClaimsFromToken(token);
				String regenToken = jwtUtil.createToken(claims);
				response.addHeader(HttpHeaders.AUTHORIZATION, bearer + regenToken);
				return true;
			} else {
				return false;
			}
		} else {
			if (requestUrl.equals("/api/v1/user/verifyEmployeeCodeAndGenerateOtp") || requestUrl.equals("/api/v1/user/login")) {

				Claims claims = Jwts.claims().setSubject(String.valueOf(LocalDateTime.now()));
				claims.put("ip", request.getRemoteAddr());

				String tkn = jwtUtil.createToken(claims);

				response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tkn);

				return true;
			}
		}

		return true;
	}

	private String extractTokenFromRequest(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7); // Exclude "Bearer " prefix to get the token
		}
		return null;
	}
}
