package com.codemind.playcenter.studentservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codemind.playcenter.studentservice.entity.User;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

	User findByUserName(String userName);

//    List<Student> findByEnabled(int isStudent);
//    
//    Student findByResetPasswordToken(String token);
//    
//    Student findByEmail(String email);

}
