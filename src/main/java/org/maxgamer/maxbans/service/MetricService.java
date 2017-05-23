package org.maxgamer.maxbans.service;

import org.bstats.Metrics;
import org.maxgamer.maxbans.MaxBansPlus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class MetricService {
    public static final String LOCALE = "locale";
    public static final String USER_BANS = "user_bans";
    public static final String USER_MUTES = "user_mutes";
    public static final String IP_BANS = "ip_bans";
    public static final String IP_MUTES = "ip_mutes";
    public static final String WARNINGS = "warnings";
    public static final String KICKS = "kicks";

    private Metrics metrics;
    private Map<String, Integer> increments = new HashMap<>();

    public MetricService(MaxBansPlus plugin) {
        metrics = new Metrics(plugin);

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

    public void increment(String chartId) {
        int value = get(chartId) + 1;
        increments.put(chartId, value);
    }
}
