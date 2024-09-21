package com.codemind.playcenter.dashboardservice.entity;

import java.time.LocalDate;
import java.util.List;

public class WebUser {

	private Integer id;
	private String userName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private String mobileNo;
	private int enabled;
	private LocalDate admissionDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(LocalDate admissionDate) {
		this.admissionDate = admissionDate;
	}

	private List<Role> roles;

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "WebUser [userName=" + userName + ", firstName=" + firstName + ", middleName=" + middleName
				+ ", lastName=" + lastName + ", email=" + email + ", mobileNo=" + mobileNo + ", roles=" + roles + "]";
	}

}
