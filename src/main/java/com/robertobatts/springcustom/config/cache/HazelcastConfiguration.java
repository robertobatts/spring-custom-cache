package com.robertobatts.springcustom.config.cache;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class HazelcastConfiguration {

    @Bean
    @Primary
    public CacheManager hazelcastCacheManager() {

        return new HazelcastCacheManager(Hazelcast.newHazelcastInstance());
    }
}