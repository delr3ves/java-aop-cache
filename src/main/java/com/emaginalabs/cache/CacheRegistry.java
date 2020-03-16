package com.emaginalabs.cache;

/**
 * In charge to maintain the cache registry and retrieve caches based on their namespace.
 *
 * @author Sergio Arroyo - @delr3ves
 */
public interface CacheRegistry {

    /**
     * Find the cache for the given namespace. In case it does not exists try to initialize.
     * Last chance, CacheRegistry will return the default cache.
     *
     * @param namespace the namespace to identify the cache+
     * @return the cache that match with the namespace.
     */
    Cache getCache(String namespace);

    /**
     * Remove cache from the registry in order to invalidate it
     *
     * @param namespace the namespace to identify the cache+
     */
    void removeCache(String namespace);
}
