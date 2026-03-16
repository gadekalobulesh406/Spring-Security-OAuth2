package com.itkedu.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itkedu.model.User;
import com.itkedu.repository.UserRepository;

@Service
public class UserDetailedService implements UserDetailsService {

	private UserRepository userRepository;
	
	public UserDetailedService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				user.isAccountNonLocked(), true, true, true,
				Collections.singleton(() -> "ROLE_" + user.getRole().name()));

	}
}