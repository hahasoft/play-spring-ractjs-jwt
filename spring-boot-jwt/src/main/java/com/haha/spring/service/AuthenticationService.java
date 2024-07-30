package com.haha.spring.service;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haha.spring.model.auth.AuthenticationRequest;
import com.haha.spring.model.auth.AuthenticationResponse;
import com.haha.spring.model.auth.RegisterRequest;
import com.haha.spring.model.token.Token;
import com.haha.spring.model.token.TokenType;
import com.haha.spring.model.user.User;
import com.haha.spring.repository.TokenRepository;
import com.haha.spring.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

//	private final UserRepository repository;
//	private final PasswordEncoder passwordEncoder;
//	private final JwtService jwtService;
//	private final AuthenticationManager authenticationManager;
//	private final RefreshTokenService refreshTokenService;
//	private final RefreshTokenRepository refreshTokenRepository;
	
	public AuthenticationResponse register(RegisterRequest request) {
		
		if (repository.existsByEmail(request.getEmail())) {
			throw new IllegalStateException("email already taken");
		}
		
		var user = User.builder()
				.firstname(request.getFirstname())
				.lastname(request.getLastname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role( request.getRole()  )
				.build();
		 
		var savedUser = repository.save(user);
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		saveUserToken(savedUser, jwtToken);
		return AuthenticationResponse.builder()
		        .accessToken(jwtToken)
		            .refreshToken(refreshToken)
		        .build();
		    
		
//		repository.save(user);
//		var jwtToken = jwtService.generateToken(user);
//		
//		var refreshToken =  refreshTokenService.createRefreshToken( String.valueOf( user.getId() ));
//		
//		return AuthenticationResponse.builder()
//				.accessToken( jwtToken )
//				.refreshToken( refreshToken.getToken() )
//				.expiryDate( refreshToken.getExpiryDate() )
//				.email( user.getEmail() )
//				.role( user.getRole().name() )
//				.build();
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate( 
				new UsernamePasswordAuthenticationToken(
						request.getEmail(), 
						request.getPassword()
				)
		);
		var user = repository.findByEmail(request.getEmail())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		return AuthenticationResponse.builder()
		        .accessToken(jwtToken)
		            .refreshToken(refreshToken)
		        .build();
//		var jwtToken = jwtService.generateToken(user);
//		var refreshToken =  refreshTokenService.createRefreshToken( String.valueOf( user.getId() ));
//		return AuthenticationResponse.builder()
//				.accessToken( jwtToken )
//				.refreshToken( refreshToken.getToken() )
//				.expiryDate( refreshToken.getExpiryDate() )
//				.email( user.getEmail() )
//				.role( user.getRole().name() )
//				.build();
	}
	
	private void saveUserToken(User user, String jwtToken) {
	    var token = Token.builder()
	        .user(user)
	        .token(jwtToken)
	        .tokenType(TokenType.BEARER)
	        .expired(false)
	        .revoked(false)
	        .build();
	    tokenRepository.save(token);
	  }

	private void revokeAllUserTokens(User user) {
	    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
	    if (validUserTokens.isEmpty())
	      return;
	    validUserTokens.forEach(token -> {
	      token.setExpired(true);
	      token.setRevoked(true);
	    });
	    tokenRepository.saveAll(validUserTokens);
	  }
	
	public void refreshToken(
	          HttpServletRequest request,
	          HttpServletResponse response
	  ) throws IOException {
	    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
	    final String refreshToken;
	    final String userEmail;
	    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
	      return;
	    }
	    refreshToken = authHeader.substring(7);
	    userEmail = jwtService.extractUsername(refreshToken);
	    if (userEmail != null) {
	      var user = this.repository.findByEmail(userEmail)
	              .orElseThrow();
	      if (jwtService.isTokenValid(refreshToken, user)) {
	        var accessToken = jwtService.generateToken(user);
	        revokeAllUserTokens(user);
	        saveUserToken(user, accessToken);
	        var authResponse = AuthenticationResponse.builder()
	                .accessToken(accessToken)
	                .refreshToken(refreshToken)
	                .build();
	        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
	      }
	    }
	  }
	
//	public AuthenticationResponse refreshToken(String refreshToken) {
//		
//		var refreshTokenObj =  refreshTokenRepository.findByToken(refreshToken).orElseThrow();
//		refreshTokenRepository.deleteById( refreshTokenObj.getId() );
//		
//		User user = repository.findById( Integer.parseInt( refreshTokenObj.getUserId() )).orElseThrow();
//		var jwtToken = jwtService.generateToken(user);
//		var newRefreshToken =  refreshTokenService.createRefreshToken( String.valueOf( user.getId() ));
//		
//		return AuthenticationResponse.builder()
//				.accessToken( jwtToken )
//				.refreshToken( newRefreshToken.getToken() )
//				.expiryDate( newRefreshToken.getExpiryDate() )
//				.email( user.getEmail() )
//				.role( user.getRole().name() )
//				.build();
//	}
		
		
		


	
//	private Role checkRole(String role) {
//		switch (role) {
//			case "USER": { return Role.USER; }
//			case "MODERATOR": {  return Role.MODERATOR; }
//			case "ADMIN": {  return Role.ADMIN; }
//			default: { return Role.USER; }
//		}
//	}
	
}
