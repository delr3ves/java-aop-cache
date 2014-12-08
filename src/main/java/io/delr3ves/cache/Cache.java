package io.delr3ves.cache;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public interface Cache {

    Object get(CachedMethodId key) throws Throwable;

    void put(CachedMethodId key, Object value);

    void put(CachedMethodId key, Throwable e);
}
