package org.maxgamer.maxbans.util;

import org.flywaydb.core.Flyway;
import org.maxgamer.maxbans.config.JdbcConfig;

/**
 * @author netherfoam
 */
public class FlywayUtil {
    public static Flyway migrater(JdbcConfig jdbc) {
        Flyway flyway = new Flyway();

        flyway.setClassLoader(Flyway.class.getClassLoader());
        flyway.setDataSource(jdbc.getUrl(), jdbc.getUsername(), jdbc.getPassword());

        // Fallback to h2 if no driver is available
        String type = "h2";
        if(jdbc.getDriver().contains("mysql")) {
            // MySQL uses a different set of migrations
            type = "mysql";
        }

        flyway.setLocations("db/migration/" + type);

        return flyway;
    }
}
