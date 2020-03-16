package com.emaginalabs.cache.dummy;

import com.emaginalabs.cache.Cached;
import com.emaginalabs.cache.fixture.CacheConfigBuilder;

import java.util.Date;
import java.util.UUID;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class DummyCachedMethods {

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE)
    public String getCachedUUIDByGuava() {
        return UUID.randomUUID().toString();
    }

    @Cached(namespace = CacheConfigBuilder.EHCAHE_NAMESPACE)
    public String getCachedUUIDByEhCache() {
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

}
