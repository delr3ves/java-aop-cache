package io.delr3ves.cache;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author Sergio Arroyo - @delr3ves
 */
@Data
public class CacheNamespaceConfig {
    public enum CacheProvider { EHCACHE, GUAVA }

    public static final int DEFAULT_MAXIUM_SIZE = 50000;
    public static final int DEFAULT_EXPIRATION = 600;
    public static final int DEFAULT_EXCEPTION_EXPIRATION = 300;
    public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;

    public static final CacheProvider DEFAULT_PROVIDER = CacheProvider.GUAVA;

    private CacheProvider provider = DEFAULT_PROVIDER;
    private Integer resultCacheSize = DEFAULT_MAXIUM_SIZE;
    private Integer resultCachettl = DEFAULT_EXPIRATION;
    private TimeUnit resultCacheTimeUnit = DEFAULT_TIMEUNIT;

    private Integer errorCachettl= DEFAULT_MAXIUM_SIZE;
    private Integer errorCacheSize = DEFAULT_EXCEPTION_EXPIRATION;
    private TimeUnit errorCacheTimeUnit = DEFAULT_TIMEUNIT;

    public Long getResultTTLInSeconds() {
        return resultCacheTimeUnit.toSeconds(resultCachettl);
    }

    public Long getErrorTTLInSeconds() {
        return errorCacheTimeUnit.toSeconds(errorCachettl);
    }
}
