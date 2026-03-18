package com.itkedu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itkedu.model.User;
import com.itkedu.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserController {


    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private  UserRepository userRepository;

    @GetMapping("/api/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> userProfile() {
        log.info("USER endpoint accessed");
        return ResponseEntity.ok("USER ACCESS");
    }

    @GetMapping("/api/moderator")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<String> moderatorAccess() {
        log.info("MODERATOR endpoint accessed");
        return ResponseEntity.ok("MODERATOR ACCESS");
    }

    @GetMapping("/api/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> adminAccess() {
        log.info("SUPER ADMIN endpoint accessed");
        return ResponseEntity.ok("SUPER ADMIN ACCESS");
    }

    @PostMapping("/admin/unlock/{username}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> unlockUser(@PathVariable String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);

        userRepository.save(user);

        log.warn("User account unlocked: {}", username);

        return ResponseEntity.ok("User unlocked successfully");
    }
}