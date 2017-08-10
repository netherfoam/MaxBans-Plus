package org.maxgamer.maxbans.service.metric;

/**
 * @author netherfoam
 */
public interface MetricService {
    String LOCALE = "locale";
    String USER_BANS = "user_bans";
    String USER_MUTES = "user_mutes";
    String IP_BANS = "ip_bans";
    String IP_MUTES = "ip_mutes";
    String WARNINGS = "warnings";
    String KICKS = "kicks";

    void increment(String chartId);
}
