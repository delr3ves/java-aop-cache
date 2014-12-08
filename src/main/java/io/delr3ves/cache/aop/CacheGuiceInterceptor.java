package io.delr3ves.cache.aop;

import io.delr3ves.cache.Cache;
import io.delr3ves.cache.CacheRegistry;
import io.delr3ves.cache.Cached;
import io.delr3ves.cache.CachedMethodId;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;

/**
 * Intercepts method invocation and look in the proper cache to avoid
 * @author Sergio Arroyo - @delr3ves
 */
public class CacheGuiceInterceptor implements MethodInterceptor {

    @Inject
    private CacheRegistry cacheRegistry;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Cached cached = invocation.getMethod().getAnnotation(Cached.class);
        CachedMethodId cacheKey = CachedMethodId.fromInvocation(invocation);
        Cache cache = getCache(cached.namespace());
        Object cachedResult = cache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        try {
            Object result = invocation.proceed();
            if (result != null) {
                cache.put(cacheKey, result);
            }
            return result;
        } catch (Throwable e) {
            if (cacheableException(e, cached)) {
                cache.put(cacheKey, e);
            }
            throw  e;
        }
    }

    private Boolean cacheableException(Throwable e, Cached cached) {
        for (Class<? extends Throwable> exception : cached.cachedExceptions()) {
            if (exception.isAssignableFrom(e.getClass())) {
                return true;
            }
        }
        return false;
    }

    private Cache getCache(String namespace) {
        return cacheRegistry.getCache(namespace);
    }
}
