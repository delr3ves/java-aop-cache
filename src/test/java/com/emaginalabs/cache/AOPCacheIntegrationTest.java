package com.emaginalabs.cache;

import com.codahale.metrics.MetricRegistry;
import com.emaginalabs.cache.dummy.DummyCachedMethods;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new AOPCacheGuiceModule(
                CacheConfigBuilder.createConfigForIntegration(), new MetricRegistry()));
        dummyCachedMethods = injector.getInstance(DummyCachedMethods.class);
        cacheInvalidator = injector.getInstance(CacheInvalidator.class);
    }

    @Test
    public void testGuavaResultsCacheShouldReturnSameResult() {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuava();
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testGuavaResultsAfterCacheIsExpiredShouldNotCacheResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuava();
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL + 1);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
    }

    @Test
    public void testEhCacheResultsCacheShouldReturnSameResult() {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCache();
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByEhCache();
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testEhCacheResultsCacheIsExpiredShouldNotReturnSameResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCache();
        Thread.sleep(CacheConfigBuilder.EHCACHE_TTL + 1);
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
    public void testGuavaResultsAfterCacheIsInvalidatedShouldNotCacheResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByGuava();
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.GUAVA_NAMESPACE);
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL - 1);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        String thirdSecondCall = dummyCachedMethods.getCachedUUIDByGuava();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
        assertThat(resultSecondCall, equalTo(thirdSecondCall));
    }

    @Test
    public void testEhCacheResultsAfterCacheIsInvalidatedShouldNotCacheResult() throws Exception {
        String resultFirstCall = dummyCachedMethods.getCachedUUIDByEhCache();
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.EHCAHE_NAMESPACE);
        Thread.sleep(CacheConfigBuilder.EHCACHE_TTL - 1);
        String resultSecondCall = dummyCachedMethods.getCachedUUIDByEhCache();
        String thirdSecondCall = dummyCachedMethods.getCachedUUIDByEhCache();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
        assertThat(resultSecondCall, equalTo(thirdSecondCall));
    }

}
