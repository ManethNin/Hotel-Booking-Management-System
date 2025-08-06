package com.maneth.zikhron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {H2ConsoleAutoConfiguration.class})
@RestController
public class ZikhronRestApplication {

	@GetMapping("/hello")
	public String hello(){
		return "Hello";
	}

	public static void main(String[] args) {

		SpringApplication.run(ZikhronRestApplication.class, args);


	}

}
