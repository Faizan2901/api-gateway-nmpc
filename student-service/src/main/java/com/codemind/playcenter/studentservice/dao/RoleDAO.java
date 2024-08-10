package com.codemind.playcenter.studentservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codemind.playcenter.studentservice.entity.Role;

@Repository
public interface RoleDAO extends JpaRepository<Role, Integer> {

	Role findByRoleDescription(String roleDesc);

}
