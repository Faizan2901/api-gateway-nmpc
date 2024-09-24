package com.codemind.playcenter.attendanceservice.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codemind.playcenter.attendanceservice.entity.StudentAttendance;

public interface StudentAttendanceDAO extends JpaRepository<StudentAttendance, Integer> {

	List<StudentAttendance> findByDate(LocalDate date);

	StudentAttendance findByStudentIdAndDate(Integer id, LocalDate date);

	@Query(value = "SELECT s.date FROM StudentAttendance s WHERE s.studentId = :id AND FUNCTION('MONTH', s.date) = :month AND FUNCTION('YEAR', s.date) = :year")
	List<Date> findAttendanceByStudentIdAndMonth(@Param("id") int id, @Param("month") int month, @Param("year") int year);

	@Query(value = "SELECT COUNT(*) FROM StudentAttendance s WHERE s.studentId = :id AND FUNCTION('MONTH', s.date) = :month AND FUNCTION('YEAR', s.date) = :year")
	int findAttendanceDaysByStudentIdAndMonth(@Param("id") int id, @Param("month") int month, @Param("year") int year);

	@Query(value = "SELECT s FROM StudentAttendance s WHERE s.studentId = :id")
	List<StudentAttendance> findAttendanceByStudentId(@Param("id") int id);
	
	@Query(value = "SELECT * FROM play_center.student_attendance order by date limit 1",nativeQuery = true)
	StudentAttendance findByFirstAttendanceMonth();
	
}
