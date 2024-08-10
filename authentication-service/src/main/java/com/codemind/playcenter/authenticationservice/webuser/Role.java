package com.codemind.playcenter.authenticationservice.webuser;

import lombok.Data;

@Data
public class Role {

//	private int id;

	private String roleDescription;

//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

}
