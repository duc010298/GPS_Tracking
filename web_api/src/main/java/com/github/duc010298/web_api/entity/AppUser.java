package com.github.duc010298.web_api.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * The persistent class for the app_user database table.
 * 
 */
@Entity
@Table(name="app_user", schema = "public")
public class AppUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private UUID userId;
	private String encryptedPassword;
	private Date tokenActiveAfter;
	private String userName;
	private List<AppRole> appRoles;

	public AppUser() {
	}


	@Id
	@org.hibernate.annotations.Type(type = "pg-uuid")
	@Column(name="user_id")
	public UUID getUserId() {
		return this.userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}


	@Column(name="encrypted_password")
	public String getEncryptedPassword() {
		return this.encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="token_active_after")
	public Date getTokenActiveAfter() {
		return this.tokenActiveAfter;
	}

	public void setTokenActiveAfter(Date tokenActiveAfter) {
		this.tokenActiveAfter = tokenActiveAfter;
	}


	@Column(name="user_name")
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	//bi-directional many-to-many association to AppRole
	@ManyToMany
	@JoinTable(
		name="user_role"
		, joinColumns={
			@JoinColumn(name="user_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="role_id")
			}
		)
	public List<AppRole> getAppRoles() {
		return this.appRoles;
	}

	public void setAppRoles(List<AppRole> appRoles) {
		this.appRoles = appRoles;
	}

}