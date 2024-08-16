package com.codemind.playcenter.attendanceservice.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.codemind.playcenter.attendanceservice.entity.Student;


@FeignClient(name="user-service")
public interface StudentProxy {
	
	@GetMapping("/users/students")
	public List<Student> getStudentsForAttendance();

}
