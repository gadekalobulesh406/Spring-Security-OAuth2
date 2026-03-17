package com.itkedu.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itkedu.model.User;
import com.itkedu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserController {

    private  UserRepository userRepository;
    
    public UserController(UserRepository userReposiroty)
    {
    	this.userRepository = userReposiroty;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/unlock/{username}")
    public String unlockUser(@PathVariable String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);

        userRepository.save(user);

        return "User unlocked successfully";
    }
}