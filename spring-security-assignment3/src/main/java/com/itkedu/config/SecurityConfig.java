package com.itkedu.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.itkedu.service.SocialAppService;

@Configuration
public class SecurityConfig {

    private  SocialAppService socialAppService;
    
    public SecurityConfig(SocialAppService socialAppService) {
    	this.socialAppService = socialAppService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/error").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )

            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                    )
            )

            .oauth2Login(oauth -> oauth
                    .loginPage("/")
                    .userInfoEndpoint(user ->
                            user.userService(socialAppService)
                    )
                    .defaultSuccessUrl("/user", true)
            )

            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}