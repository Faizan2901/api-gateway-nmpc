package com.codemind.playcenter.attendanceservice.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codemind.playcenter.attendanceservice.entity.Student;



@FeignClient(name="user-service")
public interface StudentProxy {
	
	@GetMapping("/users/students")
	public List<Student> getStudentsForAttendance();
	
	@GetMapping("/users/user")
	public Student getExististingUser(@RequestParam("name") String name);
	
	@GetMapping("/users/user-id")
	public Student getExististingUserById(@RequestParam("id") Integer id);
	

}
