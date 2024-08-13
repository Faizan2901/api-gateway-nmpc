package com.codemind.playcenter.dashboardservice.webuser;

import lombok.Data;

@Data
public class Role {

	private String roleDescription;

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

}
