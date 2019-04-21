package com.github.duc010298.web_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/TestToken")
public class TestTokenController {
	
	@GetMapping
	public String TestToken() {
		return "Success";
	}
}
