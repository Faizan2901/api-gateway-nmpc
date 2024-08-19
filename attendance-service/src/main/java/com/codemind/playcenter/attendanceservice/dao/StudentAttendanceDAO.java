package com.codemind.playcenter.attendanceservice.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codemind.playcenter.attendanceservice.entity.StudentAttendance;

public interface StudentAttendanceDAO extends JpaRepository<StudentAttendance, Integer>{
	
	List<StudentAttendance> findByDate(LocalDate date);
	
	StudentAttendance findByStudentIdAndDate(Integer id,LocalDate date);

}
