package com.itkedu.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itkedu.dto.AuthResponse;
import com.itkedu.dto.LoginRequest;
import com.itkedu.service.UserDetailedService;
import com.itkedu.utils.JWTUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private AuthenticationManager authenticationManager;
	private JWTUtils jwtUtils;
	private UserDetailedService userService;
	

	public AuthController(AuthenticationManager authenticationManager, JWTUtils jwtUtils,
			UserDetailedService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.userService = userService;
	}

	@PostMapping("/login")
	public AuthResponse login(@RequestBody LoginRequest request) {

		var userDetails = userService.loadUserByUsername(request.getUsername());

		String token = jwtUtils.generateToken(userDetails);

		return new AuthResponse(token);
	}
}