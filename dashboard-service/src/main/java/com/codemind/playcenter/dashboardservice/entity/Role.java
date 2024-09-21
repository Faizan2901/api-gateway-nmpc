package com.codemind.playcenter.dashboardservice.entity;

import java.util.Objects;

import lombok.Data;

@Data
public class Role {

	private int id;
	private String roleDescription;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id); // Compare by role ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  // Hash based on role ID
    }

}
