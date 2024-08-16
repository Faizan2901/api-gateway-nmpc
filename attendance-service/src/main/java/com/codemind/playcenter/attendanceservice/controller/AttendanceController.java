package com.codemind.playcenter.attendanceservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codemind.playcenter.attendanceservice.entity.Student;
import com.codemind.playcenter.attendanceservice.proxy.StudentProxy;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	private DefaultController defaultController;
	
	private StudentProxy studentProxy;

	@Autowired
	public AttendanceController(DefaultController defaultController,StudentProxy studentProxy) {
		this.defaultController = defaultController;
		this.studentProxy = studentProxy;
	}

	@GetMapping("/attendance-page")
	public String getAttendancePage() {
		
		String username=defaultController.getAuthenticatedUserName();
		
		List<Student> studentList=studentProxy.getStudentsForAttendance();
		
		System.out.println("Authenticated Username => "+username);

		return "/attendance-page";

	}
	

}
