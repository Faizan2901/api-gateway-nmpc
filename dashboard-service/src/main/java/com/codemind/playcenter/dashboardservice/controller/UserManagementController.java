package com.codemind.playcenter.dashboardservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codemind.playcenter.dashboardservice.proxy.UserProxy;
import com.codemind.playcenter.dashboardservice.webuser.WebUser;

@Controller
@RequestMapping("/user-management")
public class UserManagementController {

	private Logger logger = LoggerFactory.getLogger(HomeController.class);

	private UserProxy proxy;

	@Autowired
	public UserManagementController(UserProxy proxy) {
		this.proxy = proxy;
	}

	@GetMapping("/users")
	public String getUserListForManagement(Model model) {

		List<WebUser> userList=proxy.getUserForManagement(0);
		userList.addAll(proxy.getUserForManagement(1));
		System.out.println(userList);
		
		model.addAttribute("allUsers", userList);
		
		return "/user-management";

	}

}
