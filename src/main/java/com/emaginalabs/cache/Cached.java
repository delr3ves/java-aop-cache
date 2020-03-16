package com.emaginalabs.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sergio Arroyo - @delr3ves
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    String DEFAULT_NAMESPACE = "Default";
    String namespace() default DEFAULT_NAMESPACE;
    Class<? extends Throwable>[] cachedExceptions() default {};
}
