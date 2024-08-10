package com.codemind.playcenter.dashboardservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nmpc-home")
public class MainHomeController {
	
	@GetMapping("/home")
	public String getHomePage() {

		return "/home-page";
	}

}
