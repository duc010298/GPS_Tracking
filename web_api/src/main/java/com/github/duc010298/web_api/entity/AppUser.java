package com.github.duc010298.web_api.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


/**
 * The persistent class for the app_user database table.
 * 
 */
@Entity
@Table(name="app_user")
public class AppUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private UUID userId;
	private String email;
	private String encryptedPassword;
	private Timestamp expiryDate;
	private UUID resetPasswordToken;
	private Timestamp tokenActiveAfter;
	private String userName;
	private List<Device> devices;
	private List<AppRole> appRoles;

	public AppUser() {
	}


	@Id
	@Column(name="user_id")
	public UUID getUserId() {
		return this.userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	@Column(name="encrypted_password")
	public String getEncryptedPassword() {
		return this.encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}


	@Column(name="expiry_date")
	public Timestamp getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}


	@Column(name="reset_password_token")
	public UUID getResetPasswordToken() {
		return this.resetPasswordToken;
	}

	public void setResetPasswordToken(UUID resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}


	@Column(name="token_active_after")
	public Timestamp getTokenActiveAfter() {
		return this.tokenActiveAfter;
	}

	public void setTokenActiveAfter(Timestamp tokenActiveAfter) {
		this.tokenActiveAfter = tokenActiveAfter;
	}


	@Column(name="user_name")
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	//bi-directional many-to-one association to Device
	@OneToMany(mappedBy="appUser")
	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public Device addDevice(Device device) {
		getDevices().add(device);
		device.setAppUser(this);

		return device;
	}

	public Device removeDevice(Device device) {
		getDevices().remove(device);
		device.setAppUser(null);

		return device;
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