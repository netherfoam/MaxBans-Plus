package org.maxgamer.maxbans.config;

import org.bukkit.configuration.ConfigurationSection;
import org.maxgamer.maxbans.exception.ConfigException;
import org.maxgamer.maxbans.util.RestrictionUtil;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author netherfoam
 */
public class WarningConfig {
    private Map<Integer, String> penalties = new HashMap<>();
    private int strikes = 3;
    private Duration duration = Duration.ofDays(7);

    public WarningConfig() {
    }

    public WarningConfig(ConfigurationSection section) throws ConfigException {
        ConfigurationSection commands = section.getConfigurationSection("penalties");
        if(commands != null) {
            for(String key : commands.getKeys(false)) {
                String value = commands.getString(key);
                try {
                    int strike = Integer.parseInt(key);
                    penalties.put(strike, value);
                    strikes = Math.max(strike, strikes);
                } catch (NumberFormatException e) {
                    throw new ConfigException(commands, "Expected key to be an integer, got '" + key + "'");
                }
            }
        }

        int total = section.getInt("strikes", -1);
        if(total != -1) {
            if (total < strikes) {
                throw new ConfigException(section, "Number of strikes may not be less than the highest penalty strike");
            }

            // If we're given strikes, we use it. Otherwise it's implicit
            strikes = total;
        }

        String timeString = section.getString("duration", "7 days");
        Duration duration = RestrictionUtil.getDuration(new LinkedList<>(Arrays.asList(timeString.split(" "))));
        if(duration == null) {
            throw new ConfigException(section, "Expected duration like '7 days' or '1 minute', but got '" + timeString + "'");
        }
    }

    public String getPenalty(int strikes) {
        return penalties.get(strikes);
    }

    public void setPenalty(int strikes, String command) {
        penalties.put(strikes, command);
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
