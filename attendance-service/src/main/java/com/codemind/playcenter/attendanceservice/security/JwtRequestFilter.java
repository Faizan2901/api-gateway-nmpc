package com.codemind.playcenter.attendanceservice.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import com.codemind.playcenter.attendanceservice.exceptionhandling.JwtExpiredException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
        this.allowedAuthority = Arrays.asList("ROLE_TEACHER", "ROLE_ADMIN", "ROLE_MANAGER");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = getJwtFromRequest(request, authorizationHeader);

        if (jwt != null) {
            try {
                String username = extractUsername(jwt);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "",
                            new ArrayList<>());
                    List<GrantedAuthority> authorities = getAuthoritiesFromToken(jwt);
                    boolean hasStudentRole = authorities.stream()
                            .anyMatch(grantedAuthority -> allowedAuthority.contains(grantedAuthority.getAuthority()));

                    if (hasStudentRole && Boolean.TRUE.equals(validateToken(jwt, userDetails.getUsername()))) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, getAuthoritiesFromToken(jwt));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                // Log the exception if necessary
                System.out.println("JWT has expired: " + ex.getMessage());

                // Wrap and re-throw the ExpiredJwtException as your custom JwtExpiredException
                throw new JwtExpiredException(ex.getMessage());
            } catch (Exception ex) {
                // Catch any other exceptions that might occur and wrap them appropriately
                System.out.println("An error occurred during JWT processing: " + ex.getMessage());
                throw new ServletException("Failed to process JWT token", ex);
            }
        }
        chain.doFilter(request, response);
    }



    private String getJwtFromRequest(HttpServletRequest request, String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                return Arrays.stream(cookies)
                             .filter(cookie -> "JWT".equals(cookie.getName()))
                             .map(Cookie::getValue)
                             .findFirst()
                             .orElse(null);
            }
        }
        return null;
    }

    private Boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            if (extractedUsername.equals(username) && !isTokenExpired(token)) {
                return true;
            }
        } catch (ExpiredJwtException ex) {
            throw new JwtExpiredException("JWT token has expired during validation", ex);
        }
        return false;
    }

    private String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private List<GrantedAuthority> getAuthoritiesFromToken(String jwt) {
        List<String> roles = extractRoles(jwt);
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException ex) {
            throw new JwtExpiredException("JWT token has expired during expiration check", ex);
        }
    }
}
