package com.haha.spring.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
	
	@Value("${app.jwt.secret}")
	private String jwtSecretKey;
	
	@Value("${app.jwt.expirationInMs}")
	private long jwtExpire;
	
	@Value("${app.jwt.refresh-token.expirationInMs}")
	private long refreshTokenExpire;
	
	@PostConstruct
	public void init() {
		log.debug("secretKey: {} ,jwtExpire: {}, refrshTokenExpire: {}" , jwtSecretKey,jwtExpire,refreshTokenExpire);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	
	@SuppressWarnings("deprecation")
	private  Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.setSigningKey( getSigningKey() )
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	


	public String generateToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails,jwtExpire);
	}
	
	public String generateRefreshToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails,refreshTokenExpire);
	}
	
	public String buildToken(
			Map<String,Object> extraClaims,
			UserDetails userDetails ,
			long expireation
	) {
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new  Date(System.currentTimeMillis() + expireation ))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

//	public boolean validateJwtToken(String authToken) {
//	    try {
//	      Jwts.parser().setSigningKey(getSigningKey()).build().parse(authToken);
//	      return true;
//	    } catch (MalformedJwtException e) {
//	      log.error("Invalid JWT token: {}", e.getMessage());
//	    } catch (ExpiredJwtException e) {
//	      log.error("JWT token is expired: {}", e.getMessage());
//	    } catch (UnsupportedJwtException e) {
//	      log.error("JWT token is unsupported: {}", e.getMessage());
//	    } catch (IllegalArgumentException e) {
//	      log.error("JWT claims string is empty: {}", e.getMessage());
//	    }
//
//	    return false;
//	  }
	
}
