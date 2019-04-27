package com.github.duc010298.web_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.duc010298.web_api.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {
	
	Device findByImei(String imei);

}
