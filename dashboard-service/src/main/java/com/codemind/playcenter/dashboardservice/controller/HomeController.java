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
import com.codemind.playcenter.dashboardservice.entity.Role;
import com.codemind.playcenter.dashboardservice.entity.WebUser;
import com.codemind.playcenter.dashboardservice.proxy.UserProxy;

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

		WebUser user = proxy.getUser(userName);
		List<Role> assignedRoles = user.getRoles();
		Role role = proxy.getRoleById(Integer.parseInt(newRole));
		if (role.getRoleDescription().equals("ROLE_MANAGER") || role.getRoleDescription().equals("ROLE_STUDENT")
				|| role.getRoleDescription().equals("ROLE_TEACHER")) {
			user.setEnabled(0);
		}
		assignedRoles.add(role);
		user.setRoles(assignedRoles);

		proxy.updateUser(user);

		logger.info("Newly role [{}] assigned to this User Name :-> {}", role.getRoleDescription(), userName);

		return "redirect:" + applicationProperties.getApiGatewayUrl() + "/dashboard-service/nmpc/assign-role?userName="
				+ userName;
	}

	@GetMapping("/delete-role")
	public String deleteRole(@RequestParam("userName") String userName, @RequestParam("role") String roleId) {

		WebUser user = proxy.getUser(userName);
		List<Role> assignedRoles = user.getRoles();
		Role role = proxy.getRoleById(Integer.parseInt(roleId));
		assignedRoles.remove(role);
		if (role.getRoleDescription().equals("ROLE_MANAGER") || role.getRoleDescription().equals("ROLE_STUDENT")
				|| role.getRoleDescription().equals("ROLE_TEACHER")) {
			user.setEnabled(1);
		}
		user.setRoles(assignedRoles);

		proxy.updateUser(user);

		logger.info("Delete role [{}] for this User Name :-> {}", role, userName);

		return "redirect:" + applicationProperties.getApiGatewayUrl() + "/dashboard-service/nmpc/assign-role?userName="
				+ userName;
	}

}
