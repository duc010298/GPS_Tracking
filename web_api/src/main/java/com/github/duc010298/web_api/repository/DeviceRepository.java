package com.github.duc010298.web_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {
	
	Device findByImei(String imei);

	List<Device> findAllByAppUser(AppUser appUser);
}

