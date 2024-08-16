package com.codemind.playcenter.attendanceservice.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.codemind.playcenter.attendanceservice.config.ApplicationProperties;
import com.codemind.playcenter.attendanceservice.exceptionhandling.JwtExpiredException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtRequestFilter jwtRequestFilter;

	private final ApplicationProperties applicationProperties;

	@Autowired
	public SecurityConfig(JwtRequestFilter jwtRequestFilter, ApplicationProperties applicationProperties) {
		this.jwtRequestFilter = jwtRequestFilter;
		this.applicationProperties = applicationProperties;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(authorize -> authorize
	            .anyRequest().authenticated())
	        .exceptionHandling(exceptionHandling -> exceptionHandling
	            .authenticationEntryPoint((request, response, authException) -> {
	                // Check if the exception is JwtExpiredException
	                if (authException.getCause() instanceof JwtExpiredException) {
	                    // Handle JwtExpiredException specifically
//	                    System.out.println(authException.getCause().getMessage());
	                    response.sendRedirect(applicationProperties.getApiGatewayUrl() + "/authentication-service/play-center/");
	                } else {
	                    // Handle all other exceptions
	                    System.out.println(authException.getMessage());
	                    response.sendRedirect(applicationProperties.getApiGatewayUrl() + "/error-service/error/error-page");
	                }
	            }))
	        .csrf().disable()
	        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}


}
