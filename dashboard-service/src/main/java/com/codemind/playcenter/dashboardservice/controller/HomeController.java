package com.codemind.playcenter.dashboardservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.dashboardservice.config.ApplicationProperties;
import com.codemind.playcenter.dashboardservice.proxy.UserProxy;
import com.codemind.playcenter.dashboardservice.webuser.Role;
import com.codemind.playcenter.dashboardservice.webuser.WebUser;

@Controller
@RequestMapping("/nmpc")
public class HomeController {

	private Logger logger = LoggerFactory.getLogger(HomeController.class);

	private UserProxy proxy;

	private ApplicationProperties applicationProperties;

	@Autowired
	public HomeController(UserProxy proxy, ApplicationProperties applicationProperties) {
		this.proxy = proxy;
		this.applicationProperties = applicationProperties;
	}

	@GetMapping("/dashboard-page")
	public String getDashBoardPage(Model model) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		logger.info("Authenticated Username is ===> {}", username);

		WebUser webUser = proxy.getUser(username);

		logger.info("{}", webUser);

		String webusername = webUser.getFirstName() + " " + webUser.getLastName();

		List<Role> roleList = webUser.getRoles();

		boolean isManager = roleList.stream().anyMatch(role -> role.getRoleDescription().equals("ROLE_MANAGER"));
		boolean isTeacher = roleList.stream().anyMatch(role -> role.getRoleDescription().equals("ROLE_TEACHER"));

		model.addAttribute("isStudent", !isTeacher);
		model.addAttribute("isManager", isManager);
		model.addAttribute("isTeacher", isTeacher);
		model.addAttribute("webusername", webusername);

		return "/dashboard-page";
	}

	@GetMapping("/assign-role")
	public String getAssignedRolePage(@RequestParam("userName") String userName, Model model) {
		System.out.println(userName);
		WebUser user = proxy.getUser(userName);
		List<Role> assignedRoles = user.getRoles();
		List<Role> allRoles = proxy.getAllRole(); // Assuming all roles are fetched

		List<Role> availableRoles = allRoles.stream().filter(role -> !assignedRoles.contains(role))
				.collect(Collectors.toList());

		boolean noRole = availableRoles.isEmpty();

		model.addAttribute("userName", user.getUserName());
		model.addAttribute("assignedRoles", assignedRoles);
		model.addAttribute("noRole", noRole);
		model.addAttribute("availableRoles", availableRoles);

		return "/assign-role-page";
	}

	@PostMapping("/save-role")
	public String saveRoleAssignment(@RequestParam("userName") String userName,
			@RequestParam("newRole") String newRole) {

		System.out.println(userName + " " + newRole);

		return "redirect:" + applicationProperties.getApiGatewayUrl() + "/dashboard-service/nmpc/assign-role?userName="
				+ userName;
	}

}
