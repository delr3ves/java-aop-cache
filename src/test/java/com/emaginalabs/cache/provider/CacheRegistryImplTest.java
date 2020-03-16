package com.emaginalabs.cache.provider;

import com.codahale.metrics.MetricRegistry;
import com.emaginalabs.cache.Cache;
import com.emaginalabs.cache.Cached;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;
import com.emaginalabs.cache.CacheConfig;
import com.emaginalabs.cache.CacheRegistry;
import net.sf.ehcache.CacheManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CacheRegistryImplTest {

    public static final String IRRELEVANT_NAMESPACE = "irrelevantNamespace";

    @DataProvider(name = "onlyDefaultCacheCacheRegistry")
    public Object[][] onlyDefaultCacheCacheRegistry() {
        CacheRegistry cacheRegistry = new CacheRegistryImpl(new CacheConfig(),
                CacheManager.getInstance(), new MetricRegistry());
        return new Object[][]{
                { cacheRegistry }
        };
    }

    @Test(dataProvider = "onlyDefaultCacheCacheRegistry")
    public void whenCreateCacheWithDefaultConfigShouldFindDefaultCache(CacheRegistry cacheRegistry) {
        assertNotNull(cacheRegistry.getCache(Cached.DEFAULT_NAMESPACE));
    }

    @Test(dataProvider = "onlyDefaultCacheCacheRegistry")
    public void whenCreateCacheWithDefaultConfigItShouldBeOfTheDefaultProvider(CacheRegistry cacheRegistry) {
        assertTrue(cacheRegistry.getCache(Cached.DEFAULT_NAMESPACE) instanceof GuavaCacheImpl);
    }

    @Test(dataProvider = "onlyDefaultCacheCacheRegistry")
    public void whenLookForNotFoundCacheItShouldReturnTheDefaultOne(CacheRegistry cacheRegistry) {
        Cache irrelevantCache = cacheRegistry.getCache(IRRELEVANT_NAMESPACE);
        Cache defaultCache = cacheRegistry.getCache(Cached.DEFAULT_NAMESPACE);
        assertTrue(irrelevantCache == defaultCache); //want to check is the same cache
    }


    @DataProvider(name = "cacheCacheRegistry")
    public Object[][] cacheCacheRegistry() {
        CacheRegistry cacheRegistry = new CacheRegistryImpl(CacheConfigBuilder.createConfigForNamespace(IRRELEVANT_NAMESPACE),
                CacheManager.getInstance(), new MetricRegistry());
        return new Object[][]{
                { cacheRegistry }
        };
    }

    @Test(dataProvider = "cacheCacheRegistry")
    public void whenCreateCacheRegistryWithSomeNamespacesConfigShouldFindDefaultCache(CacheRegistry cacheRegistry) {
        assertNotNull(cacheRegistry.getCache(Cached.DEFAULT_NAMESPACE));
    }

    @Test(dataProvider = "cacheCacheRegistry")
    public void whenCreateCacheRegistryWithSomeNamespacesItShouldBeOfTheDefaultProvider(CacheRegistry cacheRegistry) {
        assertTrue(cacheRegistry.getCache(Cached.DEFAULT_NAMESPACE) instanceof GuavaCacheImpl);
    }

    @Test(dataProvider = "cacheCacheRegistry")
    public void whenCreateCacheRegistryWithSomeNamespacesItShouldBeOfTheConfiguredProvider(CacheRegistry cacheRegistry) {
        assertTrue(cacheRegistry.getCache(IRRELEVANT_NAMESPACE) instanceof EhCacheImpl);
    }

    @Test(dataProvider = "cacheCacheRegistry")
    public void whenCreateCacheRegistryWithSomeNamespacesItShouldReturnTheDefaultOne(CacheRegistry cacheRegistry) {
        Cache irrelevantCache = cacheRegistry.getCache(IRRELEVANT_NAMESPACE);
        Cache defaultCache = cacheRegistry.getCache(Cached.DEFAULT_NAMESPACE);
        assertThat(irrelevantCache, not(equalTo(defaultCache)));

    }

}