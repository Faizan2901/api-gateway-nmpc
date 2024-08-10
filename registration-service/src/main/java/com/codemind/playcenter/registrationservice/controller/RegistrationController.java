package com.codemind.playcenter.registrationservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codemind.playcenter.registrationservice.config.ApplicationProperties;
import com.codemind.playcenter.registrationservice.proxy.WebUserProxy;
import com.codemind.playcenter.registrationservice.user.WebUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

	private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	ApplicationProperties applicationProperties;

	@Autowired
	WebUserProxy proxy;

	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		WebUser user = proxy.getWebUserObject();
		System.out.println(user);
		model.addAttribute("webUser", user);
		return "/register";
	}

//	@GetMapping("/webuser")
//	public String getWebUser() {
//
//		WebUser user = proxy.getUser();
//		System.out.println("Hello " + user);
//
//		return null;
//	}

	@PostMapping("/registerUser")
	public String processRegisterData(@ModelAttribute("webUser") WebUser webUser, BindingResult bindingResult,
			Model model, HttpSession httpSession, HttpServletRequest httpRequest) {

		String userName = webUser.getUserName();
		String apiGatewayUrl = applicationProperties.getApiGatewayUrl();
		logger.info("Processing registration for : {}", userName);

		WebUser user = proxy.getExististingUser(userName);
		logger.info("{}", webUser);
		if (user != null) {
			logger.warn("Username already exists.");
			return "redirect:" + apiGatewayUrl + "/registration-service/registration/register?userNameExists";
		}

		proxy.save(webUser);
		logger.info("Successfully created user : " + userName);

		httpSession.setAttribute("user", webUser);

		return "redirect:" + apiGatewayUrl + "/authentication-service/play-center/?registerSuccess";

	}

}
