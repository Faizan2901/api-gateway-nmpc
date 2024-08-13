package com.codemind.playcenter.attendanceservice.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.codemind.playcenter.attendanceservice.config.ApplicationProperties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

@Configuration
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
		http.authorizeHttpRequests(authorize -> authorize
//				.requestMatchers("/nmpc-home/**", "/css/**", "/fonts/**", "/images/**", "/js/**", "/vendor/**")
//				.permitAll()
				.anyRequest().authenticated())
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint(new Http403ForbiddenEntryPoint() {
							@Override
							public void commence(HttpServletRequest request, HttpServletResponse response,
									AuthenticationException authException) throws IOException {
								response.sendRedirect(applicationProperties.getApiGatewayUrl()
										+ "/authentication-service/play-center/");
							}
						}))
				.csrf().disable().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
