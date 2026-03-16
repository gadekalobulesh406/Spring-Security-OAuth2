package com.itkedu.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

	@GetMapping("/profile")
	@PreAuthorize("hasRole('USER')")
	public String userAccess() {

		return "USER ACCESS";
	}

	@GetMapping("/moderator")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {

		return "MODERATOR ACCESS";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public String adminAccess() {

		return "SUPER ADMIN ACCESS";
	}

}