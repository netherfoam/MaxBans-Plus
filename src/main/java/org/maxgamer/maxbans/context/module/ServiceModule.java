package org.maxgamer.maxbans.context.module;

import dagger.Module;
import dagger.Provides;
import org.maxgamer.maxbans.MaxBansPlus;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.config.WarningConfig;
import org.maxgamer.maxbans.service.*;
import org.maxgamer.maxbans.service.metric.MetricService;
import org.maxgamer.maxbans.service.metric.VoidMetricService;
import org.maxgamer.maxbans.service.metric.bStatsMetricService;

/**
 * @author netherfoam
 */
@Module
public class ServiceModule {
    @Provides
    public GeoIPService geoIPService() {
        return new GeoIPService(getClass().getClassLoader().getResourceAsStream("GeoLite.zip"), "en");
    }

    /**
     * Provides a metric service. The actual implementation may be a void implementation if metrics is disabled
     * @param config the config
     * @param plugin the plugin
     * @return the metric service
     */
    @Provides
    public MetricService metricService(PluginConfig config, MaxBansPlus plugin) {
        if (!config.isMetrics()) {
            return new VoidMetricService();
        }

        return new bStatsMetricService(plugin);
    }

    @Provides
    public WarningConfig warningConfig(PluginConfig config) {
        return config.getWarningConfig();
    }
}
