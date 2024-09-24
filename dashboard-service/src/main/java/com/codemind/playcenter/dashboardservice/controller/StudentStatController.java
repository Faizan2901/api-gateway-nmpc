package com.codemind.playcenter.dashboardservice.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.dashboardservice.config.ApplicationProperties;
import com.codemind.playcenter.dashboardservice.entity.Role;
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

	@SuppressWarnings("unchecked")
	@GetMapping("/statistics")
	private String showStatistics(HttpSession httpSession, Model model) {

		List<String> months = new ArrayList<>();

		WebUser student = studentProxy.getExististingUser(defaultController.getAuthenticatedUserName());

		months = (List<String>) httpSession.getAttribute("selectedMonths");

		

		Map<WebUser, Map<String, Map<List<Date>, Integer>>> finalStatMap = new LinkedHashMap<>();

		List<Role> roles = student.getRoles();

		boolean isTeacher = roles.stream().anyMatch(role -> role.getRoleDescription().contains("TEACHER"));

		if (isTeacher) {
			List<WebUser> studentsList = studentProxy.getUserForManagement(1);

			for (WebUser user : studentsList) {
				Map<String, Map<List<Date>, Integer>> dateMonthMap = new LinkedHashMap<>();
				filledMonthWiseDatesAndDayCount(user, months, dateMonthMap);
				finalStatMap.put(user, dateMonthMap);
			}

			printStudentAttendaceStatistics(finalStatMap);

			model.addAttribute("finalStatMap", finalStatMap);
			return "/student-stat-board";
		}
		
		Map<String, Map<List<Date>, Integer>> dateMonthMap = new LinkedHashMap<>();

		filledMonthWiseDatesAndDayCount(student, months, dateMonthMap);

		finalStatMap.put(student, dateMonthMap);

		printStudentAttendaceStatistics(finalStatMap);

		model.addAttribute("finalStatMap", finalStatMap);

		return "/student-stat-board";
	}

	private void filledMonthWiseDatesAndDayCount(WebUser user, List<String> months,
			Map<String, Map<List<Date>, Integer>> dateMonthMap) {
		for (String month : months) {

			String monthPart = month.substring(0, month.indexOf("-"));
			int monthNumber = Integer.parseInt(monthMap.get(monthPart));
			int year = Integer.parseInt(month.substring(month.indexOf("-") + 1));

			List<Date> attendedDates = attendanceProxy.getAttendedDates(user.getId(), monthNumber, year);
			int attendedDayCount = attendanceProxy.getAttendedDayCount(user.getId(), monthNumber, year);

			if (attendedDayCount > 0 && attendedDates != null && !attendedDates.isEmpty()) {

				Map<List<Date>, Integer> dateCountMap = new LinkedHashMap<>();
				dateCountMap.put(attendedDates, attendedDayCount);

				dateMonthMap.put(month, dateCountMap);
			}
		}

	}

	private void printStudentAttendaceStatistics(Map<WebUser, Map<String, Map<List<Date>, Integer>>> finalStatMap) {
		for (Entry<WebUser, Map<String, Map<List<Date>, Integer>>> map : finalStatMap.entrySet()) {
			WebUser stud = map.getKey();
			Map<String, Map<List<Date>, Integer>> attendanceMap = map.getValue();

			System.out.println(stud.getFirstName() + " " + stud.getMiddleName() + " " + stud.getLastName());

			for (Entry<String, Map<List<Date>, Integer>> attendStat : attendanceMap.entrySet()) {
				String monthName = attendStat.getKey();
				Map<List<Date>, Integer> dates = attendStat.getValue();

				System.out.println("Attendance Months:- " + monthName);

				for (Entry<List<Date>, Integer> date : dates.entrySet()) {
					List<Date> attendedDays = date.getKey();
					Integer attendedDayCount = date.getValue();

					System.out.println("Attended days count:- " + attendedDayCount);
					for (Date d : attendedDays) {
						System.out.println(d.toString());
					}

				}

			}

		}

	}

}
