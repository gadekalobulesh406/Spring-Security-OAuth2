package com.itkedu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.itkedu.filters.JwtAuthenticationFilter;
import com.itkedu.filters.LoggingFilter;
import com.itkedu.service.UserDetailedService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private  JwtAuthenticationFilter jwtAuthenticationFilter;
	private  LoggingFilter loggingFilter;
	private  UserDetailedService userService;
	
	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, LoggingFilter loggingFilter, UserDetailedService userService) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.loggingFilter = loggingFilter;
		this.userService = userService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authenticationProvider(authenticationProvider())

				.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);

		provider.setPasswordEncoder(passwordEncoder());

		return provider;

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}