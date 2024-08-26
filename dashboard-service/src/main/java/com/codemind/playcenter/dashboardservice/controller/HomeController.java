package com.codemind.playcenter.dashboardservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codemind.playcenter.dashboardservice.proxy.UserProxy;
import com.codemind.playcenter.dashboardservice.webuser.Role;
import com.codemind.playcenter.dashboardservice.webuser.WebUser;

@Controller
@RequestMapping("/nmpc")
public class HomeController {

	private Logger logger = LoggerFactory.getLogger(HomeController.class);

	private UserProxy proxy;

	@Autowired
	public HomeController(UserProxy proxy) {
		this.proxy = proxy;
	}

	@GetMapping("/dashboard-page")
	public String getDashBoardPage(Model model) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		logger.info("Authenticated Username is ===> {}", username);

		WebUser webUser = proxy.getUser(username);

		logger.info("{}", webUser);

		String webusername = webUser.getFirstName() + " " + webUser.getLastName();
		
		List<Role> roleList=webUser.getRoles();
		
		boolean isManager=roleList.stream().anyMatch(role->role.getRoleDescription().equals("ROLE_MANAGER"));
		boolean isTeacher=roleList.stream().anyMatch(role->role.getRoleDescription().equals("ROLE_TEACHER"));
		
		model.addAttribute("isStudent", !isTeacher);
		model.addAttribute("isManager", isManager);
		model.addAttribute("isTeacher", isTeacher);
		model.addAttribute("webusername", webusername);

		return "/dashboard-page";
	}

}
