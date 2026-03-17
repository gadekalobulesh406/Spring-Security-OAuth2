package com.itkedu.utils;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class JWTUtils {

	private final SecretKey secretKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkey12345".getBytes());

	private static final long EXPIRATION_TIME = 86400000;

	public String generateToken(UserDetails userDetails) {

		return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(secretKey).compact();
	}

	public String extractUsername(String token) {

		return extractClaims(token, Claims::getSubject);
	}

	public <T> T extractClaims(String token, Function<Claims, T> resolver) {

		Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

		return resolver.apply(claims);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {

		String username = extractUsername(token);

		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {

		return extractClaims(token, Claims::getExpiration).before(new Date());
	}
	
	public String generateRefreshToken(UserDetails userDetails) {

	    return Jwts.builder()
	            .setSubject(userDetails.getUsername())
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)))
	            .signWith(secretKey)
	            .compact();
	}

}
