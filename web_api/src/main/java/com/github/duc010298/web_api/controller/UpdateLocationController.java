package com.github.duc010298.web_api.controller;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.duc010298.web_api.entity.UpdateLocationRequest;

@RestController
@RequestMapping(path = "/UpdateLocation")
public class UpdateLocationController {
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "text/plain;charset=UTF-8")
    public String addCustomer(@RequestBody UpdateLocationRequest updateLocationRequest) {
		System.out.println(new Date() + " " + updateLocationRequest.getImei());
		System.out.println(new Date() + " " + updateLocationRequest.getLocationHistories().size());
		return "Success";
    }
}