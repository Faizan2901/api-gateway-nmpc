package com.codemind.playcenter.dashboardservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nmpc")
public class HomeController {
	
	@GetMapping("/dashboard-page")
	public String getDashBoardPage() {

		return "/dashboard-page";
	}

}
