package com.itkedu.config;


import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.itkedu.service.SocialAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class SecurityConfig {

    private  SocialAppService socialAppService;
    
    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);
    
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
                 .accessDeniedHandler((req, res, e) -> {
                     logger.error("Access denied: {}", req.getRequestURI());
                     res.sendRedirect("/error");
                 })
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
                    .logoutSuccessHandler(oidcLogoutSuccessHandler())
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        return (request, response, authentication) -> {
        	logger.info("User logged out");
            response.sendRedirect("https://accounts.google.com/logout");
        };
    }
    }