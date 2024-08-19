package com.codemind.playcenter.attendanceservice.controller;

import java.sql.Date;
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

	@Autowired
	public AttendanceController(DefaultController defaultController, StudentProxy studentProxy,
			StudentAttendanceDAO studentAttendanceDAO, ApplicationProperties applicationProperties) {
		this.defaultController = defaultController;
		this.studentProxy = studentProxy;
		this.studentAttendanceDAO = studentAttendanceDAO;
		this.applicationProperties = applicationProperties;
	}

	@GetMapping("/attendance-page")
	public String getAttendancePage(Model model) {

		String username = defaultController.getAuthenticatedUserName();

		List<Student> studentList = studentProxy.getStudentsForAttendance();

		model.addAttribute("allStudents", studentList);

		System.out.println("Authenticated Username => " + username);

		return "/attendance-page";

	}

	@PostMapping("/fill-info")
	public String insertAttendanceInfo(
			@RequestParam(name = "selectedItems", required = false) List<String> selectedItems, Model model) {

		LocalDate date = LocalDate.now();

		List<StudentAttendance> tempStudentAttendance = studentAttendanceDAO.findByDate(date);

		if (selectedItems == null && tempStudentAttendance.isEmpty()) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/attendance-page";
		}

		if (selectedItems != null) {
			for (String selectString : selectedItems) {
				Student student = studentProxy.getExististingUser(selectString);
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

		for (StudentAttendance attendance : studentAttendance) {
			Student student = studentProxy.getExististingUserById(attendance.getStudentId());
			allStudents.add(student);
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

		studentAttendanceDAO.delete(studentAttendance);

		if (studentAttendanceDAO.findByDate(date).isEmpty()) {
			return "redirect:" + applicationProperties.getApiGatewayUrl()
					+ "/attendance-service/attendance/fill-attendance";
		}

		return "redirect:" + applicationProperties.getApiGatewayUrl()
				+ "/attendance-service/attendance/attended-student";

	}

	@GetMapping("/fill-attendance")
	private String getAllStudentForAttendance(Model model) {

		LocalDate date = LocalDate.now();

		// All User who have Student Role
		List<Student> studentList = studentProxy.getStudentsForAttendance();

		List<Student> attendedStudents = new ArrayList<>();
		List<Student> studentsNotDoneAttendance = new ArrayList<>();

		for (Student student : studentList) {
			StudentAttendance studentAttendance = studentAttendanceDAO.findByStudentIdAndDate(student.getId(), date);
			if (studentAttendance != null) {
				attendedStudents.add(student);
			} else {
				studentsNotDoneAttendance.add(student);
			}
		}

		model.addAttribute("isDoneStudent", !attendedStudents.isEmpty());
		model.addAttribute("attendanceDoneStudent", attendedStudents);
		model.addAttribute("isNotDoneAttendance", !studentsNotDoneAttendance.isEmpty());
		model.addAttribute("allStudents", studentsNotDoneAttendance);
		model.addAttribute("todayDate", date);
		return "/attendance-page";
	}

}
