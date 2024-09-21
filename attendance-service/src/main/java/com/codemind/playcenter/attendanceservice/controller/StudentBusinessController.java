package com.codemind.playcenter.attendanceservice.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codemind.playcenter.attendanceservice.config.ApplicationProperties;
import com.codemind.playcenter.attendanceservice.dao.StudentAttendanceDAO;
import com.codemind.playcenter.attendanceservice.proxy.StudentProxy;

@RestController
@RequestMapping("/student")
public class StudentBusinessController {

	private DefaultController defaultController;

	private StudentProxy studentProxy;

	private StudentAttendanceDAO studentAttendanceDAO;

	private ApplicationProperties applicationProperties;

	@Autowired
	public StudentBusinessController(DefaultController defaultController, StudentProxy studentProxy,
			StudentAttendanceDAO studentAttendanceDAO, ApplicationProperties applicationProperties) {
		this.defaultController = defaultController;
		this.studentProxy = studentProxy;
		this.studentAttendanceDAO = studentAttendanceDAO;
		this.applicationProperties = applicationProperties;
	}

	@GetMapping("/student-date")
	public List<Date> getAttendedDate(@RequestParam("id") int id, @RequestParam("month") int month,
			@RequestParam("year") int year) {
		List<Date> dates = studentAttendanceDAO.findAttendanceByStudentIdAndMonth(id, month, year);
		for (Date date : dates) {
			System.out.println(date);
		}
		return studentAttendanceDAO.findAttendanceByStudentIdAndMonth(id, month, year);
	}

	@GetMapping("/student-day-count")
	public int getAttendedDayCount(@RequestParam("id") int id, @RequestParam("month") int month,
			@RequestParam("year") int year) {
		return studentAttendanceDAO.findAttendanceDaysByStudentIdAndMonth(id, month, year);
	}

}
