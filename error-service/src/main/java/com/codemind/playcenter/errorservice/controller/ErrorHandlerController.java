package com.codemind.playcenter.errorservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorHandlerController {

	@GetMapping("/error-page")
	public String getUnauthorizedErrroPage() {

		return "/error-page";
		
	}

}
