package com.emaginalabs.cache.fixture;

import com.emaginalabs.cache.CacheConfig;
import com.emaginalabs.cache.CacheNamespaceConfig;
import com.emaginalabs.cache.Cached;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class CacheConfigBuilder {

    public static final String EHCACHE_NAMESPACE = "EHCAHE_NAMESPACE";
    public static final String GUAVA_NAMESPACE = "GUAVA";
    public static final int GUAVA_TTL = 100;
    public static final int EHCACHE_TTL = 1000;
    public static final int DEFAULT_TTL = 600;

    public static CacheConfig createConfigForNamespace(String namespace) {
        Map<String, CacheNamespaceConfig> configValues = new HashMap<String, CacheNamespaceConfig>();
        CacheNamespaceConfig value = new CacheNamespaceConfig();
        value.setProvider(CacheNamespaceConfig.CacheProvider.EHCACHE);
        configValues.put(namespace, value);
        return new CacheConfig(configValues);
    }

    public static CacheConfig createConfigForIntegration() {
        Map<String, CacheNamespaceConfig> configValues = new HashMap<String, CacheNamespaceConfig>();
        CacheNamespaceConfig ehconfig = new CacheNamespaceConfig();
        ehconfig.setProvider(CacheNamespaceConfig.CacheProvider.EHCACHE);
        ehconfig.setResultCacheTtl(EHCACHE_TTL);
        ehconfig.setResultCacheTimeUnit(TimeUnit.MILLISECONDS);
        configValues.put(EHCACHE_NAMESPACE, ehconfig);

        CacheNamespaceConfig guavaConfig = new CacheNamespaceConfig();
        guavaConfig.setProvider(CacheNamespaceConfig.CacheProvider.GUAVA);
        guavaConfig.setResultCacheTtl(GUAVA_TTL);
        guavaConfig.setResultCacheTimeUnit(TimeUnit.MILLISECONDS);
        guavaConfig.setErrorCacheTtl(GUAVA_TTL);
        guavaConfig.setErrorCacheTimeUnit(TimeUnit.MILLISECONDS);
        configValues.put(GUAVA_NAMESPACE, guavaConfig);

        CacheNamespaceConfig defaultConfig = new CacheNamespaceConfig();
        defaultConfig.setProvider(CacheNamespaceConfig.CacheProvider.GUAVA);
        defaultConfig.setResultCacheTtl(DEFAULT_TTL);
        defaultConfig.setResultCacheTimeUnit(TimeUnit.MILLISECONDS);
        defaultConfig.setErrorCacheTtl(DEFAULT_TTL);
        defaultConfig.setErrorCacheTimeUnit(TimeUnit.MILLISECONDS);
        configValues.put(Cached.DEFAULT_NAMESPACE, defaultConfig);

        return new CacheConfig(configValues);
    }
}
