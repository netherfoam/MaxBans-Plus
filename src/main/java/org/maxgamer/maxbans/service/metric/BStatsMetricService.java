package org.maxgamer.maxbans.service.metric;

import org.bstats.bukkit.Metrics;
import org.maxgamer.maxbans.MaxBansPlus;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author netherfoam
 */
public class BStatsMetricService implements MetricService {
    /**
     * bStats Link: https://bstats.org/plugin/bukkit/MaxBansPlus/829
     */
    public static final int MAXBANS_PLUS_PLUGIN_ID = 829;

    private Map<String, Integer> increments = new HashMap<>();

    @Inject
    public BStatsMetricService(MaxBansPlus plugin) {
        Metrics metrics = new Metrics(plugin, MAXBANS_PLUS_PLUGIN_ID);

        metrics.addCustomChart(new Metrics.SimplePie(LOCALE, () -> plugin.getLocale().getLocale()));
        metrics.addCustomChart(new Metrics.SingleLineChart(USER_BANS, getAndReset(USER_BANS)));
        metrics.addCustomChart(new Metrics.SingleLineChart(USER_MUTES, getAndReset(USER_MUTES)));
        metrics.addCustomChart(new Metrics.SingleLineChart(IP_BANS, getAndReset(IP_BANS)));
        metrics.addCustomChart(new Metrics.SingleLineChart(IP_MUTES, getAndReset(IP_MUTES)));
        metrics.addCustomChart(new Metrics.SingleLineChart(WARNINGS, getAndReset(WARNINGS)));
        metrics.addCustomChart(new Metrics.SingleLineChart(KICKS, getAndReset(KICKS)));
    }

    protected Callable<Integer> getAndReset(String key) {
        return () -> {
            Integer v = increments.remove(key);
            if (v == null) return 0;

            return v;
        };
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
