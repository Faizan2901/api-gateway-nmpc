package com.codemind.playcenter.attendanceservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	@GetMapping("/attendance-page")
	public String getAttendancePage() {
		System.out.println("Heloo");
		
		return "/attendance-page";

	}

}
