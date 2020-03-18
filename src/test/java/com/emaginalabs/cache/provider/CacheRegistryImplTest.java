package com.emaginalabs.cache.provider;

import com.codahale.metrics.MetricRegistry;
import com.emaginalabs.cache.*;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;
import net.sf.ehcache.CacheManager;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CacheRegistryImplTest {

    public static final String IRRELEVANT_NAMESPACE = "irrelevantNamespace";
    private CacheConfig cacheConfig;

    @DataProvider(name = "onlyDefaultCacheCacheRegistry")
    public Object[][] onlyDefaultCacheCacheRegistry() {
        cacheConfig = new CacheConfig();
        CacheRegistry cacheRegistry = new CacheRegistryImpl(cacheConfig,
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
    public void whenLookForNotFoundCacheItShouldReturnTheACacheWithGivenNamespaceButDefaultConfig(CacheRegistry cacheRegistry) {
        Cache irrelevantCache = cacheRegistry.getCache(IRRELEVANT_NAMESPACE);
        Cache defaultCache = cacheRegistry.getCache(Cached.DEFAULT_NAMESPACE);
        assertTrue(irrelevantCache != defaultCache);
        assertTrue(irrelevantCache.getNamespace() == IRRELEVANT_NAMESPACE);
        assertTrue(irrelevantCache.getConfig() == defaultCache.getConfig());
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

    @Test(dataProvider = "onlyDefaultCacheCacheRegistry")
    public void whenCreateCacheRegistryWithSomeOverriddenNamespacesItShouldReturnTheNewConfig(CacheRegistry cacheRegistry) {
        CacheNamespaceConfig newConfig = givenANewConfigForNamespace(IRRELEVANT_NAMESPACE);
        Cache irrelevantCache = cacheRegistry.getCache(IRRELEVANT_NAMESPACE);
        assertThat(irrelevantCache.getConfig(), equalTo(newConfig));
    }

    private CacheNamespaceConfig givenANewConfigForNamespace(String namespace) {
        CacheNamespaceConfig newConfig = new CacheNamespaceConfig();
        newConfig.setProvider(CacheNamespaceConfig.CacheProvider.EHCACHE);
        newConfig.setResultCacheTtl(6);
        newConfig.setResultCacheTimeUnit(TimeUnit.MILLISECONDS);
        newConfig.setErrorCacheTtl(5);
        newConfig.setErrorCacheTimeUnit(TimeUnit.MILLISECONDS);
        cacheConfig.put(namespace, newConfig);
        return newConfig;
    }

}