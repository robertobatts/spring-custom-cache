package com.robertobatts.springcustom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@CacheConfig(cacheManager = "hazelcastCacheManager")
public class SpringCustomApplication {

	@Autowired
	CacheManager cacheManager;

	public static void main(String[] args) {
		SpringApplication.run(SpringCustomApplication.class, args);
	}

}
