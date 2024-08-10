package com.codemind.playcenter.authenticationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codemind.playcenter.authenticationservice.config.ApplicationProperties;
import com.codemind.playcenter.authenticationservice.proxy.UserProxy;
import com.codemind.playcenter.authenticationservice.webuser.WebUser;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/play-center")
public class AuthenticationController {

	@Autowired
	UserProxy proxy;
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	
	SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

	@GetMapping("/")
	public String getLoginPage() {
		return "/login-page";
	}

	@GetMapping("/user/{name}")
	public String getUserDetails(@PathVariable("name") String name) {
		WebUser user = proxy.getUser(name);
		System.out.println(user);
		return "/login-page";
	}

	@PostMapping("/logout")
	public String performLogout(HttpServletRequest request, HttpServletResponse response) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null) {
	        System.out.println(authentication.getName());
	        this.logoutHandler.logout(request, response, authentication);
	    } else {
	        System.out.println("Authentication is null");
	    }
	    // Invalidate the session to ensure all session data is cleared
	    request.getSession().invalidate();
	    
	    // Clear the JWT cookie
	    Cookie jwtCookie = new Cookie("JWT", null);
	    jwtCookie.setHttpOnly(true);
	    jwtCookie.setMaxAge(0);  // Expire the cookie immediately
	    jwtCookie.setPath("/");  // Match the original path of the cookie
	    response.addCookie(jwtCookie);
	    
	    return "redirect:" + applicationProperties.getApiGatewayUrl() + "/authentication-service/play-center/";
	}

	
}
