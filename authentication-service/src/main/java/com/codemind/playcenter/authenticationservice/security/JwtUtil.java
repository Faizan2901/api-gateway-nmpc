package com.codemind.playcenter.authenticationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.codemind.playcenter.authenticationservice.config.ApplicationProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

	@Autowired
	private ApplicationProperties applicationProperties;

	private String secret;

	@PostConstruct
	public void init() {
		this.secret = applicationProperties.getAuthSecretKey();
	}

	public String generateToken(String username, List<String> roles) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles); // Add roles to the claims
		return createToken(claims, username);
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 20)) // 10 hours expiry
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	public List<String> extractRoles(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("roles", List.class);
	}

	private Boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	private List<GrantedAuthority> getAuthoritiesFromToken(String jwt) {
		List<String> roles = extractRoles(jwt);
		return roles.stream().map((String role) -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
	}

}
