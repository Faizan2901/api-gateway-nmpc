package com.codemind.playcenter.attendanceservice.entity;

import java.sql.Date;

public class StudentAttendance {

	private Integer studentId;

	private Date date;

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
