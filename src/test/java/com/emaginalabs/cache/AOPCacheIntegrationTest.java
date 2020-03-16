package com.emaginalabs.cache;

import com.codahale.metrics.MetricRegistry;
import com.emaginalabs.cache.dummy.DummyCachedMethods;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class AOPCacheIntegrationTest {

    private DummyCachedMethods dummyCachedMethods;

    @BeforeMethod
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new AOPCacheGuiceModule(
                CacheConfigBuilder.createConfigForIntegration(), new MetricRegistry()));
        dummyCachedMethods = injector.getInstance(DummyCachedMethods.class);
    }

    @Test
    public void testGuavaResultsCacheShouldReturnSameResult() {
        Date resultFirstCall = dummyCachedMethods.getCachedDateByGuava();
        Date resultSeccondCall = dummyCachedMethods.getCachedDateByGuava();
        assertThat(resultFirstCall, equalTo(resultSeccondCall));
    }

    @Test
    public void testGuavaResultsAfterCacheIsExpiredSouldNotCacheResult() throws Exception {
        Date resultFirstCall = dummyCachedMethods.getCachedDateByGuava();
        Thread.sleep(CacheConfigBuilder.GUAVA_TTL + 1);
        Date resultSeccondCall = dummyCachedMethods.getCachedDateByGuava();
        assertThat(resultFirstCall, not(equalTo(resultSeccondCall)));
    }

    @Test
    public void testEhCacheResultsCacheShouldReturnSameResult() {
        Date resultFirstCall = dummyCachedMethods.getCachedDateByEhCache();
        Date resultSeccondCall = dummyCachedMethods.getCachedDateByEhCache();
        assertThat(resultFirstCall, equalTo(resultSeccondCall));
    }

    @Test
    public void testEhCacheResultsCacheIsExpiredShouldNotReturnSameResult() throws Exception {
        Date resultFirstCall = dummyCachedMethods.getCachedDateByEhCache();
        Thread.sleep(CacheConfigBuilder.EHCACHE_TTL + 1);
        Date resultSeccondCall = dummyCachedMethods.getCachedDateByEhCache();
        assertThat(resultFirstCall, not(equalTo(resultSeccondCall)));
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

}
