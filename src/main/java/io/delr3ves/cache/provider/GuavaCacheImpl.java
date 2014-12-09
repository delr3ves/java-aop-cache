package io.delr3ves.cache.provider;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import io.delr3ves.cache.Cache;
import io.delr3ves.cache.CacheNamespaceConfig;
import io.delr3ves.cache.CachedMethodId;

import java.text.MessageFormat;

import static io.delr3ves.cache.MetricUtils.registerMetric;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class GuavaCacheImpl implements Cache {

    private com.google.common.cache.Cache resultsCache;
    private com.google.common.cache.Cache exceptionsCache;

    public GuavaCacheImpl(String namespace, CacheNamespaceConfig config, MetricRegistry metricRegistry) {
        resultsCache = CacheBuilder.newBuilder().maximumSize(config.getResultCacheSize())
                .expireAfterAccess(config.getResultCacheTtl(), config.getResultCacheTimeUnit()).recordStats().build();
        registerCacheMerics(namespace + "Results", resultsCache, metricRegistry);

        exceptionsCache = CacheBuilder.newBuilder().maximumSize(config.getErrorCacheSize())
                .expireAfterAccess(config.getErrorCachettl(), config.getErrorCacheTimeUnit()).recordStats().build();
        registerCacheMerics(namespace + "Exceptions", exceptionsCache, metricRegistry);

    }

    private void registerCacheMerics(String namespace, final com.google.common.cache.Cache cache, MetricRegistry metricRegistry) {
        final CacheStats stats = cache.stats();
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "HitCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return stats.hitCount();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "MissCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return stats.missCount();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "HitRatio"), new Gauge<Double>() {
            @Override
            public Double getValue() {
                return stats.hitRate();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "Size"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return cache.size();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "RequestCount"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return stats.requestCount();
            }
        });
        registerMetric(metricRegistry, MessageFormat.format("{0}Cache.{1}", namespace, "MissRatio"), new RatioGauge() {
            @Override
            public Ratio getRatio() {
                return Ratio.of(stats.missCount(), stats.requestCount());
            }
        });
    }

    @Override
    public Object get(CachedMethodId key) throws Throwable {
        Object cachedResult = resultsCache.getIfPresent(key);
        if (cachedResult != null) {
            return cachedResult;
        }
        lookForException(key);
        return null;
    }

    @Override
    public void put(CachedMethodId key, Object value) {
        resultsCache.put(key, value);
    }

    @Override
    public void put(CachedMethodId key, Throwable e) {
        exceptionsCache.put(key, e);

    }

    private void lookForException(CachedMethodId cacheKey) throws Throwable {
        Throwable cachedException = (Throwable) exceptionsCache.getIfPresent(cacheKey);
        if (cachedException != null) {
            throw cachedException;
        }
    }
}
