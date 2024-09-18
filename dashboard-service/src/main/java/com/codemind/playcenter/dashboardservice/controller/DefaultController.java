package com.codemind.playcenter.dashboardservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class DefaultController {
	
	public String getAuthenticatedUserName() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return authentication.getName();
        } else {
            return null;
        }

    }

}
