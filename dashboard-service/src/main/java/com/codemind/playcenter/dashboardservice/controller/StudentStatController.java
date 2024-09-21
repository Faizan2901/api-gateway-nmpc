package com.codemind.playcenter.dashboardservice.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.dashboardservice.config.ApplicationProperties;
import com.codemind.playcenter.dashboardservice.entity.WebUser;
import com.codemind.playcenter.dashboardservice.proxy.AttendanceProxy;
import com.codemind.playcenter.dashboardservice.proxy.UserProxy;
import com.codemind.playcenter.dashboardservice.utility.Utility;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentStatController {

	private DefaultController defaultController;

	private UserProxy studentProxy;

	private AttendanceProxy attendanceProxy;

	private ApplicationProperties applicationProperties;

	Map<String, String> monthMap = Utility.getMonth();

	@Autowired
	public StudentStatController(DefaultController defaultController, UserProxy studentProxy,
			ApplicationProperties applicationProperties, AttendanceProxy attendanceProxy) {
		this.defaultController = defaultController;
		this.studentProxy = studentProxy;
		this.applicationProperties = applicationProperties;
		this.attendanceProxy = attendanceProxy;
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
			admissionDate = admissionDate.plusMonths(1);
		}

		model.addAttribute("username", student.getFirstName());
		model.addAttribute("attendanceMonth", attendanceMonth);
		return "/student-attendance-board"; 
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
		return "redirect:" + applicationProperties.getApiGatewayUrl() + "/dashboard-service/student/statistics";
	}

	@GetMapping("/statistics")
	private String showStatistics(HttpSession httpSession, Model model) {

		List<String> months = new ArrayList<>();

		WebUser student = studentProxy.getExististingUser(defaultController.getAuthenticatedUserName());

		Map<String, Map<List<Date>, Integer>> dateMonthMap = new LinkedHashMap<>();

		Map<WebUser, Map<String, Map<List<Date>, Integer>>> finalStatMap = new LinkedHashMap<>();

		months = (List<String>) httpSession.getAttribute("selectedMonths");

		for (String month : months) {
			int monthNumber = Integer.parseInt(monthMap.get(month.substring(0, month.indexOf("-"))));
			int year = Integer.parseInt(month.substring(month.indexOf("-") + 1, month.length()));
			List<Date> dates = attendanceProxy.getAttendedDates(student.getId(), monthNumber, year);
			int dayCount = attendanceProxy.getAttendedDayCount(student.getId(), monthNumber, year);
			if (dayCount > 0) {
				HashMap<List<Date>, Integer> dateCountMap = new HashMap<>();
				dateCountMap.put(dates, dayCount);
				dateMonthMap.put(month, dateCountMap);
				finalStatMap.put(student, dateMonthMap);
			}
		}
		

		model.addAttribute("finalStatMap", finalStatMap);
		model.addAttribute("name", student.getFirstName() + " " + student.getLastName());
		return "/student-stat-board";
	}

}
