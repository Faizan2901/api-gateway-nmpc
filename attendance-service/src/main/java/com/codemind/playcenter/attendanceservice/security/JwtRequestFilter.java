package com.codemind.playcenter.attendanceservice.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.codemind.playcenter.attendanceservice.config.ApplicationProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private ApplicationProperties applicationProperties;

	private String secret;
	
	private List<String> allowedAuthority;

	@PostConstruct
	public void init() {
		this.secret = applicationProperties.getAuthSecretKey();
		
		allowedAuthority=new ArrayList<>();
		allowedAuthority.add("ROLE_TEACHER");
		allowedAuthority.add("ROLE_ADMIN");
		allowedAuthority.add("ROLE_MANAGER");
		
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
		} else {
			// Check for JWT in cookies
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				Optional<Cookie> jwtCookie = Arrays.stream(cookies).filter(cookie -> "JWT".equals(cookie.getName()))
						.findFirst();
				if (jwtCookie.isPresent()) {
					jwt = jwtCookie.get().getValue();
				}
			}
		}
		if (jwt != null) {
			username = extractUsername(jwt);
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "",
					new ArrayList<>());

			// Retrieve the roles of the user, assuming you have a method to do this
			List<GrantedAuthority> authorities = getAuthoritiesFromToken(jwt);

			// Check if the user has the ROLE_STUDENT authority
			boolean hasStudentRole = authorities.stream()
					.anyMatch(grantedAuthority ->  allowedAuthority.contains(grantedAuthority.getAuthority()));

			if (hasStudentRole && Boolean.TRUE.equals(validateToken(jwt, userDetails.getUsername()))) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

	private Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	private String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	private List<GrantedAuthority> getAuthoritiesFromToken(String jwt) {
		List<String> roles = extractRoles(jwt);
		return roles.stream().map((String role) -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList());
	}

	public List<String> extractRoles(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("roles", List.class);
	}

	private Boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}
}
