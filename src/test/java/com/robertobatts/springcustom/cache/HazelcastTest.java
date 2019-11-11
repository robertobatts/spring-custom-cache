package com.robertobatts.springcustom.cache;

import com.robertobatts.springcustom.config.cache.CachingApplicationConfiguration;
import com.robertobatts.springcustom.config.cache.HazelcastConfiguration;
import com.robertobatts.springcustom.domain.CustomObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HazelcastTest.ConfigurationTest.class)
public class HazelcastTest {

    @Autowired
    private DummyService dummyService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void cacheHitsAndMisses() {

        dummyService.getObject(new CustomObject(new CustomObject.PrimaryKey("123ABC", "boh"), "123D"));
        assertThat(this.dummyService.isCacheMiss()).isTrue();

        dummyService.getObject(new CustomObject(new CustomObject.PrimaryKey("123ABC", "boh"), "123D"));
        assertThat(this.dummyService.isCacheMiss()).isFalse();

    }



    @Service
    protected static class DummyService {

        private boolean cacheMiss;

        public synchronized boolean isCacheMiss() {

            boolean cacheMiss = this.cacheMiss;

            setCacheMiss(false);

            return cacheMiss;
        }

        public synchronized void setCacheMiss(boolean cacheMiss) {
            this.cacheMiss = cacheMiss;
        }

        @Cacheable("objects")
        public List<CustomObject> getObjects(List<CustomObject> objects) {
            setCacheMiss(true);
            return objects;
        }

        @Cacheable("objects")
        public CustomObject getObject(CustomObject object) {
            setCacheMiss(true);
            return object;
        }

        @Cacheable("objects")
        public CustomObject getObject(CustomObject.PrimaryKey key) {
            setCacheMiss(true);
            return new CustomObject(key, "something");
        }
    }


    protected static class ConfigurationTest extends HazelcastConfiguration {
        @Bean
        public DummyService dummyService() {
            return new DummyService();
        }
    }

}
