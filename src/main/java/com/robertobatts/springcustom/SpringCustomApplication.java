package com.robertobatts.springcustom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringCustomApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCustomApplication.class, args);
	}

}
