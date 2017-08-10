package org.maxgamer.maxbans.config;

import org.bukkit.configuration.ConfigurationSection;
import org.maxgamer.maxbans.exception.ConfigException;
import org.maxgamer.maxbans.util.RestrictionUtil;

import java.time.Duration;
import java.util.*;

/**
 * @author netherfoam
 */
public class WarningConfig {
    private Map<Integer, List<String>> penalties = new HashMap<>();
    private int strikes = 3;
    private Duration duration = Duration.ofDays(7);
    
    public WarningConfig() {
    }

    public WarningConfig(ConfigurationSection section) throws ConfigException {
        if(section == null) return;

        ConfigurationSection commands = section.getConfigurationSection("penalties");
        if(commands != null) {
            for(String key : commands.getKeys(false)) {
                List<String> value = new LinkedList<>();
                if(commands.isList(key)) {
                    value.addAll(commands.getStringList(key));
                } else {
                    value.add(commands.getString(key));
                }

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

    public List<String> getPenalty(int strikes) {
        List<String> penalties = this.penalties.get(strikes);
        if(penalties == null) return Collections.emptyList();

        return Collections.unmodifiableList(penalties);
    }

    public void setPenalties(int strikes, List<String> commands) {
        penalties.put(strikes, commands);
    }

    public void setPenalties(int strikes, String... commands) {
        setPenalties(strikes, Arrays.asList(commands));
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
