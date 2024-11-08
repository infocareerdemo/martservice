package com.mart.config;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthentication extends OncePerRequestFilter {

	private Log log = LogFactory.getLog(this.getClass());

	private JwtUtil jwtUtil = new JwtUtil();
	
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException, UsernameNotFoundException {
		
		String token = req.getHeader("Authorization");
		
		if (token == null) 
		{
			chain.doFilter(req, res);
			
		} 
		else 
		{
			token = token.replace("Bearer ", "");
			UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);

			if (authentication != null) 
			{
				chain.doFilter(req, res);
			} 
			else 
			{
				log.error("Invalid token");
				throw new UsernameNotFoundException("Invalid token in the request");
			}
		}
	}

	
	private UsernamePasswordAuthenticationToken getAuthentication(String token) 
	{
		if (token != null) 
		{
			Claims claims = jwtUtil.getClaimsFromToken(token);
			String tokenIpAddress = claims.get("ip", String.class);
			if (tokenIpAddress != null) {
				return new UsernamePasswordAuthenticationToken(tokenIpAddress, null, null);
			} else {
				String id = String.valueOf(claims.get("id"));
				if (id != null) {
					return new UsernamePasswordAuthenticationToken(id, null, null);
				}
			}
			return null;
		} else 
		{
			return null;
		}
	}

}
