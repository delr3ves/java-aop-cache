package io.delr3ves.cache.dummy;

import io.delr3ves.cache.Cached;
import io.delr3ves.cache.fixture.CacheConfigBuilder;

import java.util.Date;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class DummyCachedMethods {

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE)
    public Date getCachedDateByGuava() {
        return new Date();
    }

    @Cached(namespace = CacheConfigBuilder.EHCAHE_NAMESPACE)
    public Date getCachedDateByEhCache() {
        return new Date();
    }

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE, cachedExceptions = {Exception.class})
    public Date throwCachedException() throws Exception{
        throw new Exception(new Date().toString());
    }

    @Cached(namespace = CacheConfigBuilder.GUAVA_NAMESPACE, cachedExceptions = {NullPointerException.class})
    public Date throwNonCachedException() {
        throw new RuntimeException(new Date().toString());
    }

}
