package com.emaginalabs.cache;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.emaginalabs.cache.aop.CacheGuiceInterceptor;
import com.emaginalabs.cache.provider.CacheRegistryImpl;
import net.sf.ehcache.CacheManager;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class AOPCacheGuiceModule extends AbstractModule {

    private CacheConfig cacheConfig;
    private MetricRegistry metricRegistry;

    public AOPCacheGuiceModule(CacheConfig cacheConfig) {
        this(cacheConfig, new MetricRegistry());
    }

    public AOPCacheGuiceModule(CacheConfig cacheConfig, MetricRegistry metricRegistry) {
        this.cacheConfig = cacheConfig;
        this.metricRegistry = metricRegistry;
    }

    @Override
    protected void configure() {
        bind(CacheConfig.class).toInstance(cacheConfig);
        bind(CacheManager.class).toInstance(CacheManager.getInstance());
        bind(CacheRegistry.class).to(CacheRegistryImpl.class);
        bind(MetricRegistry.class).toInstance(metricRegistry);
        CacheGuiceInterceptor cachingInterceptor = new CacheGuiceInterceptor();
        requestInjection(cachingInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cached.class), cachingInterceptor);
    }

}
