package org.maxgamer.maxbans.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.util.db.BotchedUninstallDetectionCallback;
import org.maxgamer.maxbans.util.db.Flyway_MySQL_V1_1__Fix_Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author netherfoam
 */
public class FlywayUtil {
    public static Flyway migrater(JdbcConfig jdbc) {
        Flyway flyway = new Flyway();

        flyway.setClassLoader(Flyway.class.getClassLoader());
        flyway.setDataSource(jdbc.getUrl(), jdbc.getUsername(), jdbc.getPassword());

        List<FlywayCallback> callbacks = new ArrayList<>();

        // Fallback to h2 if no driver is available
        String type = "h2";
        if(jdbc.getDriver().contains("mysql")) {
            // MySQL uses a different set of migrations
            type = "mysql";

            // This allows use of databases which have existing tables in their database
            flyway.setBaselineVersion(MigrationVersion.fromVersion("1.0"));
            flyway.setBaselineOnMigrate(true);

            // We disable validation on migration v1.1 because of a legacy bug
            callbacks.add(new Flyway_MySQL_V1_1__Fix_Callback());

            // We ensure that the standard tables are available, eg. this stops users who have tampered with their database
            callbacks.add(new BotchedUninstallDetectionCallback());
        }

        flyway.setCallbacks(callbacks.toArray(new FlywayCallback[callbacks.size()]));
        flyway.setLocations("db/migration/" + type);

        return flyway;
    }
}
