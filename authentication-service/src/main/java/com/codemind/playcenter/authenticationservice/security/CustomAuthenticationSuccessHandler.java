package com.codemind.playcenter.authenticationservice.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.codemind.playcenter.authenticationservice.config.ApplicationProperties;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private ApplicationProperties applicationProperties;

	private String redirectUrl;

	@PostConstruct
	public void init() {
		this.redirectUrl = applicationProperties.getApiGatewayUrl();
	}

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String username = authentication.getName();
		String token = jwtUtil.generateToken(username);

		// Set JWT as a cookie
		Cookie jwtCookie = new Cookie("JWT", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setSecure(true); // Ensure the cookie is only sent over HTTPS
		jwtCookie.setPath("/"); // Set the path for the cookie
		jwtCookie.setMaxAge(60 * 60 * 5); // Set the expiry to match the JWT's expiry time

		response.addCookie(jwtCookie);

		response.setHeader("Authorization", "Bearer " + token);

		// Customize the redirection URL based on your requirements
		response.sendRedirect(redirectUrl + "/dashboard-service/nmpc/dashboard-page"); // Redirect to the home-page

	}
}
