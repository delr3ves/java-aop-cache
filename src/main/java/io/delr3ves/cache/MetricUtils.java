package io.delr3ves.cache;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class MetricUtils {

    public static void registerMetric(MetricRegistry metricRegistry, String name, Metric metric) {
        try {
            if (metricRegistry.getMetrics().get(name) == null) {
                metricRegistry.register(name, metric);
            }
        } catch (IllegalArgumentException e) {
            //Just not register the metric because it is already registered
        }
    }
}
