package com.emaginalabs.cache;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public interface Cache {

    String getNamespace();

    CacheNamespaceConfig getConfig();

    Object get(CachedMethodId key) throws Throwable;

    void put(CachedMethodId key, Object value);

    void put(CachedMethodId key, Throwable e);

    void invalidate();
}
