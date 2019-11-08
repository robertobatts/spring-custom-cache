package com.robertobatts.springcustom.cache;

import com.robertobatts.springcustom.config.cache.CachingApplicationConfiguration;
import com.robertobatts.springcustom.domain.CustomObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CachingCollectionElementsIndividuallyTest.ConfigurationTest.class)
public class CachingCollectionElementsIndividuallyTest {

    @Autowired
    private DummyService dummyService;

    @Test
    public void cacheHitsAndMisses() {

        List<CustomObject> objects = new ArrayList<CustomObject>() {{
            add(new CustomObject(new CustomObject.PrimaryKey("123ABC", "boh"), "123D"));
            add(new CustomObject(new CustomObject.PrimaryKey("QWERTY", "ciao"), "76567D"));
        }};

        dummyService.getObjects(objects);
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
    }


    protected static class ConfigurationTest extends CachingApplicationConfiguration {
        @Bean
        public DummyService calculatorService() {
            return new DummyService();
        }
    }

}
