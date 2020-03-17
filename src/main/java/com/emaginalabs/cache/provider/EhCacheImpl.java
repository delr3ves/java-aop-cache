package com.emaginalabs.cache.provider;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.emaginalabs.cache.Cache;
import com.emaginalabs.cache.CacheNamespaceConfig;
import com.emaginalabs.cache.CachedMethodId;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.statistics.StatisticsGateway;

import java.text.MessageFormat;

import static com.emaginalabs.cache.MetricUtils.registerMetric;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class EhCacheImpl implements Cache {
    net.sf.ehcache.Cache resultsCache;
    net.sf.ehcache.Cache exceptionsCache;

    public EhCacheImpl(String namespace, CacheNamespaceConfig config,
                       MetricRegistry metricRegistry, CacheManager cacheManager) {
        resultsCache = initializeCache(config.getResultCacheSize(), config.getResultTTLInSeconds(),
                metricRegistry, cacheManager, namespace + "Results");
        exceptionsCache = initializeCache(config.getErrorCacheSize(), config.getErrorTTLInSeconds(),
                metricRegistry, cacheManager, namespace + "Exceptions");
    }

    @Override
    public Object get(CachedMethodId key) throws Throwable {
        Element cachedResult = resultsCache.get(key);
        if (cachedResult != null) {
            return cachedResult.getObjectValue();
        }
        lookForException(key);
        return null;
    }

    @Override
    public void put(CachedMethodId key, Object value) {
        Element cacheElement = new Element(key, value);
        resultsCache.put(cacheElement);
    }

    @Override
    public void put(CachedMethodId key, Throwable e) {
        Element cacheElement = new Element(key, e);
        exceptionsCache.put(cacheElement);
    }

    @Override
    public void invalidate() {
        resultsCache.removeAll();
        exceptionsCache.removeAll();
    }

    private void lookForException(CachedMethodId cacheKey) throws Throwable {
        Element cachedException = exceptionsCache.get(cacheKey);
        if (cachedException != null) {
            throw (Throwable) cachedException.getObjectValue();
        }
    }

    private net.sf.ehcache.Cache initializeCache(Integer cacheSize, Long ttl, MetricRegistry metricRegistry, CacheManager cacheManager, String cacheName) {
        if (cacheManager.cacheExists(cacheName)) {
            return cacheManager.getCache(cacheName);
        }
        net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(new CacheConfiguration(cacheName, cacheSize)
                .eternal(false)
                .timeToLiveSeconds(ttl));
        cacheManager.addCache(cache);
        registerCacheMetrics(cacheName, cache, metricRegistry);
        return cache;
    }

    private void registerCacheMetrics(String namespace, final net.sf.ehcache.Cache cache, MetricRegistry metricRegistry) {
        final StatisticsGateway stats = cache.getStatistics();
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "HitCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return stats.cacheHitCount();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "MissCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return stats.cacheMissCount();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "HitRatio"), new Gauge<Double>() {
            @Override
            public Double getValue() {
                return stats.cacheHitRatio();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "Size"), new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return cache.getSize();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "HeapSizeInBytes"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return stats.getLocalHeapSizeInBytes();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "MissRatio"), new RatioGauge() {
            @Override
            public Ratio getRatio() {
                return Ratio.of(stats.cacheMissCount(), stats.getSize());
            }
        });
    }

}
