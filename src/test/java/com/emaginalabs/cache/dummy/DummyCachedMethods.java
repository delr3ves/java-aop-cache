package com.emaginalabs.cache.dummy;

import com.emaginalabs.cache.Cached;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;

import java.util.UUID;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class DummyCachedMethods {

    public final static String NAMESPACE_NON_CONFIGURED_ON_INIT = "NAMESPACE_NON_CONFIGURED_ON_INIT";

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE)
    public String getCachedUUIDByGuava() {
        return UUID.randomUUID().toString();
    }

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE)
    public String getCachedUUIDByGuavaWithArguments(String argument) {
        return UUID.randomUUID().toString();
    }

    @Cached(namespace = CacheConfigBuilder.EHCACHE_NAMESPACE)
    public String getCachedUUIDByEhCache() {
        return UUID.randomUUID().toString();
    }

    @Cached(namespace = CacheConfigBuilder.EHCACHE_NAMESPACE)
    public String getCachedUUIDByEhCacheWithArguments(String argument) {
        return UUID.randomUUID().toString();
    }

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE, cachedExceptions = {Exception.class})
    public String throwCachedException() throws Exception{
        throw new Exception(UUID.randomUUID().toString());
    }

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE, cachedExceptions = {NullPointerException.class})
    public String throwNonCachedException() {
        throw new RuntimeException(UUID.randomUUID().toString());
    }

    @Cached(namespace = NAMESPACE_NON_CONFIGURED_ON_INIT)
    public String getCachedUUID() {
        return UUID.randomUUID().toString();
    }

}
