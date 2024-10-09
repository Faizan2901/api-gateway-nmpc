package com.codemind.playcenter.attendanceservice.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.attendanceservice.config.ApplicationProperties;
import com.codemind.playcenter.attendanceservice.dao.StudentAttendanceDAO;
import com.codemind.playcenter.attendanceservice.entity.Student;
import com.codemind.playcenter.attendanceservice.entity.StudentAttendance;
import com.codemind.playcenter.attendanceservice.proxy.StudentProxy;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	private DefaultController defaultController;

	private StudentProxy studentProxy;

	private StudentAttendanceDAO studentAttendanceDAO;

	private ApplicationProperties applicationProperties;

	private StudentBusinessController studentBusinessController;

	@Autowired
	public AttendanceController(DefaultController defaultController, StudentProxy studentProxy,
			StudentAttendanceDAO studentAttendanceDAO, ApplicationProperties applicationProperties,
			StudentBusinessController studentBusinessController) {
		this.defaultController = defaultController;
		this.studentProxy = studentProxy;
		this.studentAttendanceDAO = studentAttendanceDAO;
		this.applicationProperties = applicationProperties;
		this.studentBusinessController = studentBusinessController;
	}

	@GetMapping("/attendance-page")
	public String getAttendancePage(Model model,
			@RequestParam(value = "todayDate", required = false) String todayDateStr) {

		LocalDate date;

		// Parse the date from the request parameter if present, otherwise use current
		// date
		if (todayDateStr != null) {
			date = LocalDate.parse(todayDateStr); // Assuming the date format is correct (ISO)
		} else {
			date = LocalDate.now();
		}

		List<StudentAttendance> tempStudentAttendance = studentAttendanceDAO.findByDate(date);

		String username = defaultController.getAuthenticatedUserName();

		List<Student> allstudentList = studentProxy.getStudentsForAttendance();

		if (!tempStudentAttendance.isEmpty()) {

			List<Student> studentList = tempStudentAttendance.stream()
					.map(a -> studentProxy.getExististingUserById(a.getStudentId())).collect(Collectors.toList());
			allstudentList.removeIf(allStud -> studentList.stream().anyMatch(stud -> stud.getId() == allStud.getId()));
			model.addAttribute("isAlreadyAttended", true);
		}

		if (allstudentList.isEmpty()) {
			model.addAttribute("allPresent", true);
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attended-student";
		}
		model.addAttribute("allStudents", allstudentList);
		model.addAttribute("todayDate", date);
		System.out.println("Authenticated Username => " + username);

		return "/attendance-page";

	}

	@PostMapping("/fill-info")
	public String insertAttendanceInfo(
			@RequestParam(name = "selectedItems", required = false) List<String> selectedItems,
			@RequestParam(name = "attendanceDate", required = false) String attendanceDateStr, Model model) {

		// Use provided date or fallback to the current date
		LocalDate date;
		if (attendanceDateStr != null && !attendanceDateStr.isEmpty()) {
			date = LocalDate.parse(attendanceDateStr); // Expecting date in "yyyy-MM-dd" format
		} else {
			date = LocalDate.now();
		}

		List<StudentAttendance> tempStudentAttendance = studentAttendanceDAO.findByDate(date);

		// Check for empty selectedItems and existing attendance records
		if ((selectedItems == null || selectedItems.isEmpty()) && tempStudentAttendance.isEmpty()) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attendance-page";
		}

		// Process the selected students for attendance
		if (selectedItems != null && !selectedItems.isEmpty()) {
			for (String selectString : selectedItems) {
				Student student = studentProxy.getExististingUser(selectString);
				StudentAttendance dbStoredStudentAttendance = studentAttendanceDAO
						.findByStudentIdAndDate(student.getId(), date);
				if (dbStoredStudentAttendance != null) {
					continue; // Skip already recorded attendance
				}
				StudentAttendance studentAttendance = new StudentAttendance();
				studentAttendance.setStudentId(student.getId());
				studentAttendance.setDate(Date.valueOf(date)); // Save the provided or current date
				studentAttendanceDAO.save(studentAttendance);
			}
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attended-student?todayDate=" + date;
		} else {
			// If selectedItems is null, set a model attribute and redirect
			model.addAttribute("isNull", true);
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attended-student?todayDate=" + date;
		}
	}

	@GetMapping("/attended-student")
	public String showAttendedStudentList(@RequestParam(value = "todayDate", required = false) String todayDateStr,
			Model model) {
		LocalDate date;

		// Parse the date from the request parameter if present, otherwise use current
		// date
		if (todayDateStr != null) {
			date = LocalDate.parse(todayDateStr); // Assuming the date format is correct (ISO)
		} else {
			date = LocalDate.now();
		}

		List<StudentAttendance> studentAttendance = studentAttendanceDAO.findByDate(date);

		List<Student> allStudents = new ArrayList<>();

		List<Student> totalStudents = studentProxy.getStudentsForAttendance();

		for (StudentAttendance attendance : studentAttendance) {
			Student student = studentProxy.getExististingUserById(attendance.getStudentId());
			allStudents.add(student);
		}

		if (totalStudents.size() != allStudents.size()) {
			model.addAttribute("allPresent", true);
		} else if (totalStudents.size() == allStudents.size()) {
			model.addAttribute("fullPresent", "Today, All Students are present.");
		}

		model.addAttribute("allStudents", allStudents);
		model.addAttribute("todayDate", date);

		return "/attended-student-list";
	}

	@GetMapping("/deleteUser")
	public String deleteStudentFromAttendanceList(@RequestParam("studentId") String id,
			@RequestParam("date") String date) {

		LocalDate attendanceDate = LocalDate.parse(date); // Parse the date string to LocalDate

		Student student = studentProxy.getExististingUserById(Integer.parseInt(id));

		// Find student attendance by student ID and the given date
		StudentAttendance studentAttendance = studentAttendanceDAO.findByStudentIdAndDate(student.getId(),
				attendanceDate);

		// Delete attendance record by ID
		studentAttendanceDAO.deleteById(studentAttendance.getId());

		// Redirect to attendance page if no more records exist for the given date,
		// otherwise redirect to attended students
		if (studentAttendanceDAO.findByDate(attendanceDate).isEmpty()) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attendance-page?todayDate=" + date;
		}

		return "redirect:" + applicationProperties.getApiGatewayUrl()
				+ "/attendance-service/attendance/attended-student?todayDate=" + date;
	}

	@GetMapping("/student-attendance-board")
	private String getStudentInfo(Model model) {
		String authenticateUserName = defaultController.getAuthenticatedUserName();

		List<Student> students = studentProxy.getStudentsForAttendance();
		for (Student s : students) {
			System.out.println(s);
		}

		StudentAttendance studentAttendance = studentBusinessController.getAttendaceMonth();
		Date firstDateOfAttendance = studentAttendance.getDate();
		LocalDate admissionDate = firstDateOfAttendance.toLocalDate();

		int dbFirstMonth = admissionDate.getMonthValue();

		LocalDate currentDate = LocalDate.now();

		int currentMonth = currentDate.getMonthValue();

		List<String> attendanceMonth = new ArrayList<>();
		for (int i = dbFirstMonth; i <= currentMonth; i++) {
			attendanceMonth.add(admissionDate.getMonth() + "-" + admissionDate.getYear());
			admissionDate = admissionDate.plusMonths(1);
		}

		model.addAttribute("username", authenticateUserName);
		model.addAttribute("attendanceMonth", attendanceMonth);
		return "/student-attendance-board";
	}

	@PostMapping("/updation")
	private String showStatistics(
			@RequestParam(name = "selectedUpdationMonth", required = false) String selectedUpdationMonth, Model model,
			HttpSession httpSession) {

		if (selectedUpdationMonth == null) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/student-attendance-board";
		}

		// Parse the month and year from the input string "JANUARY-2024"
		YearMonth yearMonth = parseMonthYear(selectedUpdationMonth);

		// Generate the list of days for the given month and year
		List<LocalDate> daysInMonth = getDaysInMonth(yearMonth);

		// Add the month and year for display in the template
		model.addAttribute("selectedMonth", yearMonth.getMonth().name() + "-" + yearMonth.getYear());
		model.addAttribute("daysInMonth", daysInMonth);
		model.addAttribute("selectedUpdationMonth", selectedUpdationMonth);
		httpSession.setAttribute("selectedUpdationMonths", selectedUpdationMonth);
		return "/show-calendar";
	}

	// Method to parse the month-year string (e.g., "JANUARY-2024")
	private YearMonth parseMonthYear(String monthYear) {
		// Split the string into month and year
		String[] parts = monthYear.split("-");
		String monthName = parts[0]; // e.g., "JANUARY"
		int year = Integer.parseInt(parts[1]); // e.g., 2024

		// Convert month name to Month enum
		Month month = Month.valueOf(monthName.toUpperCase());

		// Return a YearMonth object
		return YearMonth.of(year, month.getValue());
	}

	// Method to get all days of the given month
	private List<LocalDate> getDaysInMonth(YearMonth yearMonth) {
		List<LocalDate> days = new ArrayList<>();
		for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
			LocalDate currentDay = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), day);

			days.add(currentDay);
			if (currentDay.equals(LocalDate.now())) {
				break;
			}

		}
		return days;
	}

	@PostMapping("/show-attendance")
	public String getAttendanceUpdationPage(@RequestParam("date") String date, Model model) {
		LocalDate attendanceDate = LocalDate.of(Integer.parseInt(date.split("-")[0]),
				Integer.parseInt(date.split("-")[1]), Integer.parseInt(date.split("-")[2]));
		System.out.println(attendanceDate);
		List<StudentAttendance> tempStudentAttendance = studentAttendanceDAO.findByDate(attendanceDate);

		String username = defaultController.getAuthenticatedUserName();

		List<Student> allstudentList = studentProxy.getStudentsForAttendance();

		if (!tempStudentAttendance.isEmpty()) {
			List<Student> studentList = tempStudentAttendance.stream()
					.map(a -> studentProxy.getExististingUserById(a.getStudentId())).collect(Collectors.toList());
			allstudentList.removeIf(allStud -> studentList.stream().anyMatch(stud -> stud.getId() == allStud.getId()));
			model.addAttribute("isAlreadyAttended", true);
		}

		if (allstudentList.isEmpty()) {
			model.addAttribute("allPresent", true);
			// Pass the date as a query parameter
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attended-student?todayDate=" + attendanceDate;
		}

		model.addAttribute("todayDate", attendanceDate);
		model.addAttribute("allStudents", allstudentList);

		System.out.println("Authenticated Username => " + username);

		return "/attendance-page";
	}

}
