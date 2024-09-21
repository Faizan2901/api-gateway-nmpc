package com.codemind.playcenter.dashboardservice.proxy;

import java.sql.Date;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "attendance-service")
public interface AttendanceProxy {

	@GetMapping("/student/student-date")
	public List<Date> getAttendedDates(@RequestParam("id") int id, @RequestParam("month") int month,
			@RequestParam("year") int year);

	@GetMapping("/student/student-day-count")
	public int getAttendedDayCount(@RequestParam("id") int id, @RequestParam("month") int month,
			@RequestParam("year") int year);
}
