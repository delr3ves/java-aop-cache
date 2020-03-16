package com.emaginalabs.cache;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author Sergio Arroyo - @delr3ves
 */
@Data
public class CacheNamespaceConfig {
    public enum CacheProvider { EHCACHE, GUAVA }

    public static final int DEFAULT_MAXIMUM_SIZE = 50000;
    public static final int DEFAULT_EXPIRATION = 600;
    public static final int DEFAULT_EXCEPTION_EXPIRATION = 300;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    public static final CacheProvider DEFAULT_PROVIDER = CacheProvider.GUAVA;

    private CacheProvider provider = DEFAULT_PROVIDER;
    private Integer resultCacheSize = DEFAULT_MAXIMUM_SIZE;
    private Integer resultCacheTtl = DEFAULT_EXPIRATION;
    private TimeUnit resultCacheTimeUnit = DEFAULT_TIME_UNIT;

    private Integer errorCacheTtl = DEFAULT_MAXIMUM_SIZE;
    private Integer errorCacheSize = DEFAULT_EXCEPTION_EXPIRATION;
    private TimeUnit errorCacheTimeUnit = DEFAULT_TIME_UNIT;

    public Long getResultTTLInSeconds() {
        return resultCacheTimeUnit.toSeconds(resultCacheTtl);
    }

    public Long getErrorTTLInSeconds() {
        return errorCacheTimeUnit.toSeconds(errorCacheTtl);
    }
}
