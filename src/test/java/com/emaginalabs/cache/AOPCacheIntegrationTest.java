package com.emaginalabs.cache;

import com.codahale.metrics.MetricRegistry;
import com.emaginalabs.cache.dummy.DummyCachedMethods;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class AOPCacheIntegrationTest {

    private DummyCachedMethods dummyCachedMethods;
    private CacheInvalidator cacheInvalidator;

    @BeforeMethod
    public void setUp() {
        Injector injector = Guice.createInjector(new AOPCacheGuiceModule(
                CacheConfigBuilder.createConfigForIntegration(), new MetricRegistry()));
        dummyCachedMethods = injector.getInstance(DummyCachedMethods.class);
        cacheInvalidator = injector.getInstance(CacheInvalidator.class);
    }

    @AfterMethod
    public void tearDown() {
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.GUAVA_NAMESPACE);
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.EHCACHE_NAMESPACE);
    }

    @Test
    public void testGuavaResultsCacheShouldReturnSameResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuava();
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL / 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testGuavaResultsCacheShouldReturnSameResultWithSameArguments() throws Exception {
        String argument = "argument";
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuavaWithArguments(argument);
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL / 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuavaWithArguments(argument);
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testGuavaResultsCacheShouldNotReturnSameResultWithDifferentArguments() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuavaWithArguments("argument1");
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL / 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuavaWithArguments("argument2");
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
    }

    @Test
    public void testGuavaResultsAfterCacheIsExpiredShouldNotCacheResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuava();
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL + 1);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
    }

    @Test
    public void testEhCacheResultsCacheShouldReturnSameResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCache();
        Thread.sleep(CacheConfigBuilder.EHCACHE_TTL / 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByEhCache();
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testEhCacheResultsCacheShouldReturnSameResultWithSameArguments() throws Exception {
        String argument = "irrelevant argument";
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCacheWithArguments(argument);
        Thread.sleep(CacheConfigBuilder.EHCACHE_TTL / 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByEhCacheWithArguments(argument);
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testEhCacheResultsCacheShouldNotReturnSameResultWithDifferentArguments() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCacheWithArguments("argument1");
        Thread.sleep(CacheConfigBuilder.EHCACHE_TTL / 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByEhCacheWithArguments("argument2");
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
    }

    @Test
    public void testEhCacheResultsCacheIsExpiredShouldNotReturnSameResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCache();
        Thread.sleep(CacheConfigBuilder.EHCACHE_TTL + 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByEhCache();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
    }

    @Test
    public void testGuavaExceptionsCachedShouldThrowSameException() throws Exception {
        Exception e1 = new Exception();
        try {
            dummyCachedMethods.throwCachedException();
        } catch (Exception e) {
            e1 = e;
        }
        Exception e2 = new Exception();
        try {
            dummyCachedMethods.throwCachedException();
        } catch (Exception e) {
            e2 = e;
        }
        assertTrue(e1 == e2);
    }

    @Test
    public void testGuavaExceptionsCachedIsExpiredShouldNotThrowSameException() throws Exception {
        Exception e1 = new Exception();
        try {
            dummyCachedMethods.throwCachedException();
        } catch (Exception e) {
            e1 = e;
        }
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL + 1);
        Exception e2 = new Exception();
        try {
            dummyCachedMethods.throwCachedException();
        } catch (Exception e) {
            e2 = e;
        }
        assertTrue(e1 != e2);
    }

    @Test
    public void testGuavaNonCachedExceptionsShouldNotThrowSameException() throws Exception {
        Exception e1 = new Exception();
        try {
            dummyCachedMethods.throwNonCachedException();
        } catch (RuntimeException e) {
            e1 = e;
        }
        Thread.sleep(1);

        Exception e2 = new Exception();
        try {
            dummyCachedMethods.throwNonCachedException();
        } catch (RuntimeException e) {
            e2 = e;
        }
        assertTrue(e1 != e2);
    }

    @Test
    public void testGuavaResultsAfterCacheIsInvalidatedShouldNotCacheResult() {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuava();
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.GUAVA_NAMESPACE);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        String thirdSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
        assertThat(resultSecondCall, equalTo(thirdSecondCall));
    }

    @Test
    public void testEhCacheResultsAfterCacheIsInvalidatedShouldNotCacheResult() {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCache();
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.EHCACHE_NAMESPACE);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByEhCache();
        String thirdSecondCall = dummyCachedMethods.getCachedUUIDByEhCache();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
        assertThat(resultSecondCall, equalTo(thirdSecondCall));
    }
}
