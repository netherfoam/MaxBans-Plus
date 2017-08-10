package org.maxgamer.maxbans.service.metric;

/**
 * @author netherfoam
 */
public class VoidMetricService implements MetricService {
    @Override
    public void increment(String chartId) {
        // Nothing: Void means we don't do anything with the metrics
    }
}
