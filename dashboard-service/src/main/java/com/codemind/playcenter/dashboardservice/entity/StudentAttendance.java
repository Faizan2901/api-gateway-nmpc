package com.codemind.playcenter.dashboardservice.entity;

import java.sql.Date;

public class StudentAttendance {

	private Integer id;

	private Integer studentId;

	private Date date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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
