package org.maxgamer.maxbans.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author netherfoam
 */
public enum Lockdown {
    OFF("off", "none", "never", "nobody", "disabled"),
    NEW("new", "recent"),
    JOIN("join", "on", "joining", "login", "enabled"),
    ALL("all", "everyone");

    /**
     * Get the lockdown type for the given name
     * @param name the name of the lockdown
     * @return the lockdown type or null if the name is invalid
     */
    public static Lockdown get(String name) {
        for(Lockdown l : values()) {
            if(l.aliases.contains(name.toLowerCase())) return l;
        }

        return null;
    }

    private Set<String> aliases;

    Lockdown(String... names) {
        aliases = new HashSet<>(names.length);
        for(String s : names) {
            aliases.add(s.toLowerCase());
        }
    }

    public String description() {
        switch (this) {
            case ALL:
                return "All users are kicked and nobody may rejoin";
            case JOIN:
                return "Currently active users can play, but nobody may join";
            case NEW:
                return "Accounts created in the last 30 minutes or newly created can't join";
            case OFF:
                return "Anyone can join";
        }

        return name().toString();
    }
}
