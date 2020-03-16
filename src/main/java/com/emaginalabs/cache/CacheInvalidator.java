package com.emaginalabs.cache;

import javax.inject.Inject;

public class CacheInvalidator {

    private CacheRegistry cacheRegistry;

    @Inject
    public CacheInvalidator(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public void invalidateNamespace(String namespace) {
        cacheRegistry.removeCache(namespace);
    }
}
