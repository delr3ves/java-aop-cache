package com.emaginalabs.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sergio Arroyo - @delr3ves
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class CachedMethodId {
    private Method method;
    private List<Object> arguments;

    public static CachedMethodId fromInvocation(MethodInvocation invocation) {
        return new CachedMethodId(invocation.getMethod(),
                Arrays.asList(invocation.getArguments()));
    }
}
