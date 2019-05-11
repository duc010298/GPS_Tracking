package com.github.duc010298.web_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.duc010298.web_api.entity.Device;
import com.github.duc010298.web_api.entity.LocationHistory;

public interface LocationHistoryRepository extends JpaRepository<LocationHistory, String> {
	List<LocationHistory> findAllByDeviceOrderByTimeTrackingDesc(Device device);
}
