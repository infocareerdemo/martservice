package com.mart.config;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private final long expirationTime = 30;

	private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	public Claims getClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(JwtUtil.SECRET).parseClaimsJws(token).getBody();	
	}
	
	public boolean isTokenExpired(String token) {
		Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		Date expiration = claims.getExpiration();
		return expiration.before(new Date());
	}
	
	   public String createToken(String username) {
	        return Jwts.builder()
	                .setSubject(username) 
	                .setIssuedAt(new Date(System.currentTimeMillis())) 
	                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 60000)) 
	                .signWith(getSignKey(), SignatureAlgorithm.HS256) 
	                .compact();
	    }

	  
	public Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	

}
