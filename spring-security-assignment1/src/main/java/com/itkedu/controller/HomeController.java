package com.itkedu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/home")
	public String welcomePage() {
		return "Welcome to my page";
	}
	
	@GetMapping("/dashboard")
	public String dashboard() {
		return "The is secured page";
	}
}
