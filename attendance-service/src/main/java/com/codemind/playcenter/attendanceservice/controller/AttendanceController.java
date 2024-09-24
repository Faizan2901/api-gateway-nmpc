package com.codemind.playcenter.attendanceservice.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
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
	public String getAttendancePage(Model model) {

		LocalDate date = LocalDate.now();

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

		System.out.println("Authenticated Username => " + username);

		return "/attendance-page";

	}

	@PostMapping("/fill-info")
	public String insertAttendanceInfo(
			@RequestParam(name = "selectedItems", required = false) List<String> selectedItems, Model model) {

		LocalDate date = LocalDate.now();

		List<StudentAttendance> tempStudentAttendance = studentAttendanceDAO.findByDate(date);

		if (selectedItems == null && tempStudentAttendance.isEmpty() && selectedItems.size() == 0) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attendance-page";
		}

		if (selectedItems != null) {
			for (String selectString : selectedItems) {
				Student student = studentProxy.getExististingUser(selectString);
				StudentAttendance dbStoredStudentAttendance = studentAttendanceDAO
						.findByStudentIdAndDate(student.getId(), date);
				if (dbStoredStudentAttendance != null) {
					continue;
				}
				StudentAttendance studentAttendance = new StudentAttendance();
				studentAttendance.setStudentId(student.getId());
				studentAttendance.setDate(Date.valueOf(date));
				studentAttendanceDAO.save(studentAttendance);
			}
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attended-student";

		} else {
			model.addAttribute("isNull", true);
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attended-student";
		}
	}

	@GetMapping("/attended-student")
	private String showAttendedStudentList(Model model) {

		List<StudentAttendance> studentAttendance = studentAttendanceDAO.findByDate(LocalDate.now());

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
		model.addAttribute("todayDate", LocalDate.now());

		return "/attended-student-list";
	}

	@GetMapping("/deleteUser")
	String deleteStudentFromAttendanceList(@RequestParam("studentId") String id) {

		LocalDate date = LocalDate.now();

		Student student = studentProxy.getExististingUserById(Integer.parseInt(id));

		StudentAttendance studentAttendance = studentAttendanceDAO.findByStudentIdAndDate(student.getId(), date);

		studentAttendanceDAO.deleteById(studentAttendance.getId());

		if (studentAttendanceDAO.findByDate(date).isEmpty()) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attendance-page";
		}

		return "redirect:" + applicationProperties.getApiGatewayUrl()
				+ "/attendance-service/attendance/attended-student";

	}

	@GetMapping("/student-attendance-board")
	private String getStudentInfo(Model model) {
		String authenticateUserName = defaultController.getAuthenticatedUserName();
		
		List<Student> students=studentProxy.getStudentsForAttendance();
		for(Student s:students) {
			System.out.println(s);
		}
		
		StudentAttendance studentAttendance = studentBusinessController.getAttendaceMonth();
		Date firstDateOfAttendance = studentAttendance.getDate();
		LocalDate admissionDate = firstDateOfAttendance.toLocalDate();

		LocalDate currentDate = LocalDate.now();

		List<String> attendanceMonth = new ArrayList<>();
		while (admissionDate.isBefore(currentDate)) {
			attendanceMonth.add(admissionDate.getMonth() + "-" + admissionDate.getYear());
			admissionDate = admissionDate.plusMonths(1);
		}

		model.addAttribute("username", authenticateUserName);
		model.addAttribute("attendanceMonth", attendanceMonth);
		return "/student-attendance-board";
	}

}
