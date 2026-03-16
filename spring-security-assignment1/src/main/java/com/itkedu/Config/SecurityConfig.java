package com.itkedu.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		 http
         .authorizeHttpRequests(auth -> auth
                 .requestMatchers("/home").permitAll()
                 .anyRequest().authenticated()
         )
         .formLogin(Customizer.withDefaults())   
         .httpBasic(Customizer.withDefaults());  

     return http.build();
	}
	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	
	  @Bean
	   public UserDetailsService userDetailsService() {
		
		UserDetails user = User.builder()
				.username("admin")
				.password(passwordEncoder().encode("12345"))
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}

}
