package org.maxgamer.maxbans.service.metric;

import org.bstats.Metrics;
import org.maxgamer.maxbans.MaxBansPlus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class bStatsMetricService implements MetricService {
    private Map<String, Integer> increments = new HashMap<>();

    @Inject
    public bStatsMetricService(MaxBansPlus plugin) {
        Metrics metrics = new Metrics(plugin);

        metrics.addCustomChart(new Metrics.SimplePie(LOCALE) {
            @Override
            public String getValue() {
                return plugin.getLocale().getLocale();
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart(USER_BANS) {
            @Override
            public int getValue() {
                return getAndReset(chartId);
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart(USER_MUTES) {
            @Override
            public int getValue() {
                return getAndReset(chartId);
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart(IP_BANS) {
            @Override
            public int getValue() {
                return getAndReset(chartId);
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart(IP_MUTES) {
            @Override
            public int getValue() {
                return getAndReset(chartId);
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart(WARNINGS) {
            @Override
            public int getValue() {
                return getAndReset(chartId);
            }
        });

        metrics.addCustomChart(new Metrics.SingleLineChart(KICKS) {
            @Override
            public int getValue() {
                return getAndReset(chartId);
            }
        });
    }

    protected int getAndReset(String key) {
        Integer v = increments.remove(key);
        if(v == null) return 0;

        return v;
    }

    public int get(String key) {
        Integer v = increments.get(key);
        if(v == null) return 0;

        return v;
    }

    @Override
    public void increment(String chartId) {
        int value = get(chartId) + 1;
        increments.put(chartId, value);
    }
}
