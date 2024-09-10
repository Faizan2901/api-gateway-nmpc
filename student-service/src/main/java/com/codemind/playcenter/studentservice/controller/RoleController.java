package com.codemind.playcenter.studentservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codemind.playcenter.studentservice.dao.RoleDAO;
import com.codemind.playcenter.studentservice.entity.Role;

@RestController
@RequestMapping("/role")
public class RoleController {

	@Autowired
	RoleDAO roleDAO;

	@GetMapping("/allRole")
	public List<Role> getAllRole() {

		return roleDAO.findAll();

	}

}
