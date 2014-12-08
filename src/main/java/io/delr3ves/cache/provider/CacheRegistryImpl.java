package io.delr3ves.cache.provider;

import com.codahale.metrics.MetricRegistry;
import io.delr3ves.cache.*;
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
            cache = cacheRegistry.get(Cached.DEFAULT_NAMESPACE);
        }
        return cache;
    }

    private void initializeCaches() {
        for(Map.Entry<String, CacheNamespaceConfig> entry: cacheConfig.entrySet()) {
            initializeCache(entry.getKey(), entry.getValue());
        }
    }

    private void initializeCache(String namespace, CacheNamespaceConfig config) {
        CacheNamespaceConfig.CacheProvider provider = config.getProvider();
        if (provider.equals(CacheNamespaceConfig.CacheProvider.GUAVA)) {
            cacheRegistry.put(namespace, new GuavaCacheImpl(namespace, config, metricRegistry));
        } else if (provider.equals(CacheNamespaceConfig.CacheProvider.EHCACHE)) {
            cacheRegistry.put(namespace, new EhCacheImpl(namespace, config, metricRegistry, cacheManager));
        }
    }


}
