package com.github.duc010298.web_api.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the app_role database table.
 * 
 */
@Entity
@Table(name="app_role")
public class AppRole implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long roleId;
	private String roleName;
	private List<AppUser> appUsers;

	public AppRole() {
	}


	@Id
	@Column(name="role_id")
	public Long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}


	@Column(name="role_name")
	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	//bi-directional many-to-many association to AppUser
	@ManyToMany(mappedBy="appRoles")
	public List<AppUser> getAppUsers() {
		return this.appUsers;
	}

	public void setAppUsers(List<AppUser> appUsers) {
		this.appUsers = appUsers;
	}

}