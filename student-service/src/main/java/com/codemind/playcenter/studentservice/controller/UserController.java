package com.codemind.playcenter.studentservice.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codemind.playcenter.studentservice.dao.RoleDAO;
import com.codemind.playcenter.studentservice.dao.UserDAO;
import com.codemind.playcenter.studentservice.entity.User;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserDAO userDAO;

	@Autowired
	RoleDAO roleDAO;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/user/{name}")
	public User getUser(@PathVariable("name") String name) {
		System.out.println(name);
		return userDAO.findByUserName(name);
	}

	@GetMapping("/user")
	public User getExististingUser(@RequestParam("name") String name) {
		System.out.println(name);
		return userDAO.findByUserName(name);
	}

	@GetMapping("/user-id")
	public User getExististingUserById(@RequestParam("id") Integer id) {
		System.out.println(id);
		return userDAO.findById(id).get();
	}

//	@GetMapping("/user/{name}")
//	public User getUser(@PathVariable("name") String name) {
//		System.out.println(name);
//		return userDAO.findByUserName(name);
//	}

	@GetMapping("/webuser")
	public User getWebUserObject() {
		return new User();
	}

	@GetMapping("/")
	public List<User> getAllStudents() {

		return userDAO.findAll();
	}

	@GetMapping("/students")
	public List<User> getStudentsForAttendance() {
		return userDAO.findByEnabled(1);
	}

	@GetMapping("/user-list")
	public List<User> getUserForManagement(@RequestParam("id") int id) {
		return userDAO.findByEnabled(id);
	}

	@PostMapping("/user")
	public void save(@RequestBody User webUser) {

		LocalDate date = LocalDate.parse("2024-06-01");
		System.out.println(webUser);
		User user = new User();
		user.setUserName(webUser.getUserName());
		user.setPassword(bCryptPasswordEncoder.encode(webUser.getPassword()));
		user.setFirstName(webUser.getFirstName());
		user.setMiddleName(webUser.getMiddleName());
		user.setMobileNo(webUser.getMobileNo());
		user.setLastName(webUser.getLastName());
		user.setEmail(webUser.getEmail());
		user.setEnabled(1);
		user.setAdmissionDate(date);

		user.setRoles(Arrays.asList(roleDAO.findByRoleDescription("ROLE_STUDENT")));

		userDAO.save(user);

//		sendEmail(webUser.getEmail(), webUser.getFirstName(), httpRequest);

	}

}
