package com.emaginalabs.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergio Arroyo - @delr3ves
 */

public class CacheConfig extends HashMap<String, CacheNamespaceConfig> {

    public CacheConfig() {
        super();
        ensureDefault();
    }

    public CacheConfig(Map<? extends String, ? extends CacheNamespaceConfig> m) {
        super(m);
        ensureDefault();
    }

    private void ensureDefault() {
        if (!containsKey(Cached.DEFAULT_NAMESPACE)) {
            put(Cached.DEFAULT_NAMESPACE, new CacheNamespaceConfig());
        }
    }

}
