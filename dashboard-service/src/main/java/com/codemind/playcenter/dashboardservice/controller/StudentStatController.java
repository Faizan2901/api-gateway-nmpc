package com.codemind.playcenter.dashboardservice.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.dashboardservice.config.ApplicationProperties;
import com.codemind.playcenter.dashboardservice.proxy.UserProxy;
import com.codemind.playcenter.dashboardservice.webuser.WebUser;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentStatController {

	private DefaultController defaultController;

	private UserProxy studentProxy;

	private ApplicationProperties applicationProperties;

	@Autowired
	public StudentStatController(DefaultController defaultController, UserProxy studentProxy,
			ApplicationProperties applicationProperties) {
		this.defaultController = defaultController;
		this.studentProxy = studentProxy;
		this.applicationProperties = applicationProperties;
	}

	@GetMapping("/student-attendance-board")
	private String getStudentInfo(Model model) {
		String authenticateUserName = defaultController.getAuthenticatedUserName();
		WebUser student = studentProxy.getExististingUser(authenticateUserName);

		LocalDate admissionDate = student.getAdmissionDate();
		LocalDate date2 = LocalDate.now();
		List<String> attendanceMonth = new ArrayList<>();
		while (admissionDate.isBefore(date2)) {
			attendanceMonth.add(admissionDate.getMonth() + "-" + admissionDate.getYear());
			admissionDate = admissionDate.plusMonths(1); // Use plusMonths(1) instead
		}

		model.addAttribute("username", student.getFirstName());
		model.addAttribute("attendanceMonth", attendanceMonth);
		return "/student-attendance-board"; // Remove hardcoded path
	}

	@PostMapping("/show-statistics")
	private String showStatistics(@RequestParam(name = "selectedMonths", required = false) List<String> selectedMonths,
			Model model, HttpSession httpSession) {

		if (selectedMonths == null) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/dashboard-service/student/student-attendance-board";
		}

		model.addAttribute("selectedMonths", selectedMonths);
		httpSession.setAttribute("selectedMonths", selectedMonths);
		return "redirect:/student/statistics";
	}

}
