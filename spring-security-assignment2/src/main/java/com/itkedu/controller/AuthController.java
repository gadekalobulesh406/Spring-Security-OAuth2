package com.itkedu.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itkedu.config.SecurityConstants;
import com.itkedu.dto.AuthResponse;
import com.itkedu.dto.LoginRequest;
import com.itkedu.model.User;
import com.itkedu.repository.UserRepository;
import com.itkedu.service.UserDetailedService;
import com.itkedu.utils.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final PasswordEncoder passwordEncoder;

	private AuthenticationManager authenticationManager;
	private JWTUtils jwtUtils;
	private UserDetailedService userService;
	 private  UserRepository userRepository;
	 

	public AuthController(AuthenticationManager authenticationManager, JWTUtils jwtUtils,
			UserDetailedService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.userService = userService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
		
		log.info("Login attempt for user: {}", request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // SUCCESS → reset attempts
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            user.setFailedAttempts(0);
            user.setAccountNonLocked(true);
            userRepository.save(user);
            log.info("Login SUCCESS for user: {}", request.getUsername());

            var userDetails = userService.loadUserByUsername(request.getUsername());

            String token = jwtUtils.generateToken(userDetails);

            return new AuthResponse(token);

        } catch (BadCredentialsException ex) {

            // FAILED LOGIN  increment attempts
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);

            if (user != null) {
                int attempts = user.getFailedAttempts() + 1;
                user.setFailedAttempts(attempts);
                log.warn("Failed login attempt {} for user {}", attempts, request.getUsername());
                if (attempts >= SecurityConstants.MAX_ATTEMPTS) {
                    user.setAccountNonLocked(false);
                    log.error("Account LOCKED for user {}", request.getUsername());
                }

                userRepository.save(user);

            }

            throw new RuntimeException("Invalid username or password");
        }
    }
}