package com.emaginalabs.cache;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test suite for CacheNamespaceConfig
 */
public class CacheNamespaceConfigTest {

    @DataProvider(name = "conversionProvider")
    public Object[][] conversionProvider() {
        return new Object[][]{
                {1, TimeUnit.SECONDS, 1l},
                {1, TimeUnit.MILLISECONDS, 0l},
                {1, TimeUnit.MICROSECONDS, 0l},
                {1, TimeUnit.MINUTES, 60l},
                {1000, TimeUnit.MILLISECONDS, 1l},
                {1000000, TimeUnit.MICROSECONDS, 1l},
                {1, TimeUnit.HOURS, 3600l},
        };
    }

    @Test(dataProvider = "conversionProvider")
    public void testTTLResultConversion(Integer ttl, TimeUnit timeUnit, Long expected) {
        CacheNamespaceConfig config = new CacheNamespaceConfig();
        config.setResultCacheTtl(ttl);
        config.setResultCacheTimeUnit(timeUnit);
        assertThat(config.getResultTTLInSeconds(), equalTo(expected));
    }

    @Test(dataProvider = "conversionProvider")
    public void testTTLErrorConversion(Integer ttl, TimeUnit timeUnit, Long expected) {
        CacheNamespaceConfig config = new CacheNamespaceConfig();
        config.setErrorCacheTtl(ttl);
        config.setErrorCacheTimeUnit(timeUnit);
        assertThat(config.getErrorTTLInSeconds(), equalTo(expected));
    }
}