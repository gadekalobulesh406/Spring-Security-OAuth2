package com.itkedu.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)

			throws ServletException, IOException {

		logger.info("Request {} {}", request.getMethod(), request.getRequestURI());

		filterChain.doFilter(request, response);

		logger.info("Response Status {}", response.getStatus());
	}
}
