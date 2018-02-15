package org.maxgamer.maxbans.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.util.db.Flyway_MySQL_V1_1__Fix_Callback;

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

            // This allows use of databases which have existing tables in their database
            flyway.setBaselineVersion(MigrationVersion.fromVersion("1.0"));
            flyway.setBaselineOnMigrate(true);

            // We disable validation on migration v1.1 because of a legacy bug
            flyway.setCallbacks(new Flyway_MySQL_V1_1__Fix_Callback());
        }

        flyway.setLocations("db/migration/" + type);

        return flyway;
    }
}
