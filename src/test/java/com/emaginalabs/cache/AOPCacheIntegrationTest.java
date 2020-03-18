package com.emaginalabs.cache;

import com.codahale.metrics.MetricRegistry;
import com.emaginalabs.cache.dummy.DummyCachedMethods;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

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
    private CacheConfig config;

    @BeforeMethod
    public void setUp() {
        config = CacheConfigBuilder.createConfigForIntegration();
        Injector injector = Guice.createInjector(new AOPCacheGuiceModule(
                config, new MetricRegistry()));
        dummyCachedMethods = injector.getInstance(DummyCachedMethods.class);
        cacheInvalidator = injector.getInstance(CacheInvalidator.class);
    }

    @AfterMethod
    public void tearDown() {
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.GUAVA_NAMESPACE);
        cacheInvalidator.invalidateNamespace(CacheConfigBuilder.EHCACHE_NAMESPACE);
        cacheInvalidator.invalidateNamespace(DummyCachedMethods.NAMESPACE_NON_CONFIGURED_ON_INIT);
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

    @Test
    public void testCacheNonConfiguredNamespaceShouldCacheInADifferentCacheThanDefault() {
        String resultFirstCall = dummyCachedMethods.getCachedUUID();
        cacheInvalidator.invalidateNamespace(Cached.DEFAULT_NAMESPACE);
        String resultSecondCall = dummyCachedMethods.getCachedUUID();
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testCacheNonConfiguredNamespaceShouldWithDefaultConfig() throws Exception{
        String resultFirstCall = dummyCachedMethods.getCachedUUID();
        Thread.sleep(config.get(Cached.DEFAULT_NAMESPACE).getResultCacheTtl() / 2);
        String resultSecondCall = dummyCachedMethods.getCachedUUID();
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testExpiredCacheNonConfiguredNamespaceShouldWithDefaultConfig() throws Exception{
        String resultFirstCall = dummyCachedMethods.getCachedUUID();
        Thread.sleep(config.get(Cached.DEFAULT_NAMESPACE).getResultCacheTtl() + 1);
        String resultSecondCall = dummyCachedMethods.getCachedUUID();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
    }

    @Test
    public void testCacheWithNewConfiguredNamespaceShouldCacheInADifferentWithNewConfig() throws Exception{
        givenANewConfigForNamespace(DummyCachedMethods.NAMESPACE_NON_CONFIGURED_ON_INIT);
        String resultFirstCall = dummyCachedMethods.getCachedUUID();
        int lessTimeThanExpectedTTL = config.get(Cached.DEFAULT_NAMESPACE).getResultCacheTtl() + 1;
        Thread.sleep(lessTimeThanExpectedTTL);
        String resultSecondCall = dummyCachedMethods.getCachedUUID();
        assertThat(resultFirstCall, equalTo(resultSecondCall));
    }

    @Test
    public void testExpiredCacheWithNewConfiguredNamespaceShouldCacheInADifferentWithNewConfig() throws Exception{
        Integer newTTL = givenANewConfigForNamespace(DummyCachedMethods.NAMESPACE_NON_CONFIGURED_ON_INIT);

        String resultFirstCall = dummyCachedMethods.getCachedUUID();
        Thread.sleep(newTTL + 1);
        String resultSecondCall = dummyCachedMethods.getCachedUUID();
        assertThat(resultFirstCall, not(equalTo(resultSecondCall)));
    }

    private Integer givenANewConfigForNamespace(String namespace) {
        CacheNamespaceConfig newConfig = new CacheNamespaceConfig();
        newConfig.setProvider(CacheNamespaceConfig.CacheProvider.EHCACHE);
        Integer defaultCacheTTL = config.get(Cached.DEFAULT_NAMESPACE).getResultCacheTtl();
        Integer newTTL = defaultCacheTTL * 3;
        newConfig.setResultCacheTtl(newTTL);
        newConfig.setResultCacheTimeUnit(TimeUnit.MILLISECONDS);
        newConfig.setErrorCacheTtl(newTTL);
        newConfig.setErrorCacheTimeUnit(TimeUnit.MILLISECONDS);
        config.put(namespace, newConfig);
        return newTTL;
    }

}
