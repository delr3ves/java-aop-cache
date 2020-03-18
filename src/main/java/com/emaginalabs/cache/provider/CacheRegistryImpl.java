package com.emaginalabs.cache.provider;

import com.codahale.metrics.MetricRegistry;
import com.emaginalabs.cache.*;
import net.sf.ehcache.CacheManager;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class CacheRegistryImpl implements CacheRegistry {

    private CacheConfig cacheConfig;
    private CacheManager cacheManager;
    private MetricRegistry metricRegistry;

    private Map<String, Cache> cacheRegistry;

    @Inject
    public CacheRegistryImpl(CacheConfig cacheConfig, CacheManager cacheManager,
                             MetricRegistry metricRegistry) {
        this.cacheConfig = cacheConfig;
        this.cacheManager = cacheManager;
        this.metricRegistry = metricRegistry;
        this.cacheRegistry = new ConcurrentHashMap<String, Cache>();
        initializeCaches();
    }

    @Override
    public Cache getCache(String namespace) {
        Cache cache = cacheRegistry.get(namespace);
        if (cache == null) {
            CacheNamespaceConfig config = getCacheConfigOrDefault(namespace);
            cache = initializeCache(namespace, config);
        }
        return cache;
    }

    @Override
    public void invalidateCache(final String namespace) {
        Cache cache = this.cacheRegistry.get(namespace);
        if (cache != null) {
            cache.invalidate();
        }
    }

    private void initializeCaches() {
        for (Map.Entry<String, CacheNamespaceConfig> entry : cacheConfig.entrySet()) {
            initializeCache(entry.getKey(), entry.getValue());
        }
    }

    private Cache initializeCache(String namespace, CacheNamespaceConfig config) {
        CacheNamespaceConfig.CacheProvider provider = config.getProvider();
        Cache cache = null;
        if (provider.equals(CacheNamespaceConfig.CacheProvider.GUAVA)) {
            cache = new GuavaCacheImpl(namespace, config, metricRegistry);
        } else if (provider.equals(CacheNamespaceConfig.CacheProvider.EHCACHE)) {
            cache = new EhCacheImpl(namespace, config, metricRegistry, cacheManager);
        }
        cacheRegistry.put(namespace, cache);
        return cache;
    }

    private CacheNamespaceConfig getCacheConfigOrDefault(String namespace) {
        CacheNamespaceConfig config = cacheConfig.get(namespace);
        if (config == null) {
            config = cacheConfig.get(Cached.DEFAULT_NAMESPACE);
        }
        return config;
    }

}
