package com.emaginalabs.cache.fixture;

import com.emaginalabs.cache.CacheConfig;
import com.emaginalabs.cache.CacheNamespaceConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class CacheConfigBuilder {

    public static final String EHCAHE_NAMESPACE = "EHCAHE_NAMESPACE";
    public static final String GUAVA_NAMESPACE = "GUAVA";
    public static final int GUAVA_TTL = 10;
    public static final int EHCACHE_TTL = 1000;

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
        configValues.put(EHCAHE_NAMESPACE, ehconfig);

        CacheNamespaceConfig guavaConfig = new CacheNamespaceConfig();
        guavaConfig.setProvider(CacheNamespaceConfig.CacheProvider.GUAVA);
        guavaConfig.setResultCacheTtl(GUAVA_TTL);
        guavaConfig.setResultCacheTimeUnit(TimeUnit.MILLISECONDS);
        guavaConfig.setErrorCacheTtl(GUAVA_TTL);
        guavaConfig.setErrorCacheTimeUnit(TimeUnit.MILLISECONDS);
        configValues.put(GUAVA_NAMESPACE, guavaConfig);

        return new CacheConfig(configValues);
    }
}
