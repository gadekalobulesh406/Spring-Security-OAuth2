package com.itkedu.filters;

import com.itkedu.service.UserDetailedService;
import com.itkedu.utils.JWTUtils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JWTUtils jwtUtils;
	private UserDetailedService UserDetailedService;

	public JwtAuthenticationFilter(UserDetailedService UserDetailedService, JWTUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
		this.UserDetailedService = UserDetailedService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                               HttpServletResponse response,
	                               FilterChain filterChain)
	        throws ServletException, IOException {

	    String header = request.getHeader("Authorization");

	    try {

	        if (header != null && header.startsWith("Bearer ")) {

	            String token = header.substring(7);
	            String username = jwtUtils.extractUsername(token);

	            if (username != null &&
	                    SecurityContextHolder.getContext().getAuthentication() == null) {

	                var userDetails = UserDetailedService.loadUserByUsername(username);

	                if (jwtUtils.isTokenValid(token, userDetails)) {

	                    var authToken = new UsernamePasswordAuthenticationToken(
	                            userDetails, null, userDetails.getAuthorities());

	                    SecurityContextHolder.getContext().setAuthentication(authToken);
	                }
	            }
	        }

	        filterChain.doFilter(request, response);

	    } catch (ExpiredJwtException ex) {

	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("JWT Token Expired");
	        return;

	    } catch (Exception ex) {

	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("Invalid JWT Token");
	        return;
	    }
	}
}
