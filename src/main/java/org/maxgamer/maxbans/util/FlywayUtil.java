package org.maxgamer.maxbans.util;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.BaseFlywayCallback;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.maxgamer.maxbans.config.JdbcConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        }

        flyway.setLocations("db/migration/" + type);

        // We disable validation on migration because of a legacy bug
        flyway.setCallbacks(new BaseFlywayCallback() {
            @Override
            public void beforeValidate(Connection connection) {
                try (PreparedStatement ps = connection.prepareStatement("SELECT sv.checksum FROM schema_version sv WHERE sv.version = ?")) {
                    ps.setString(1, "1.0");
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getLong("sv.checksum") != 0L) {
                            // Repair old script checksums
                            flyway.repair();
                        }
                    }
                } catch (SQLException e) {
                    throw new FlywayException("Could not verify first migration checksum", e);
                }
            }
        });

        return flyway;
    }
}
