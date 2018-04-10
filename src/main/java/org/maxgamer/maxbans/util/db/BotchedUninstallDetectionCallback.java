package org.maxgamer.maxbans.util.db;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.callback.BaseFlywayCallback;
import org.maxgamer.maxbans.exception.SchemaBrokenException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Ensures that a server admin hasn't removed a typical table, but forgotten to remove the schema_version table
 */
public class BotchedUninstallDetectionCallback extends BaseFlywayCallback {
    public static final String MINIMUM_MIGRATION_VERSION = "1.1";
    public static final String[] REQUIRED_TABLES = {
            "Users", "Mute", "Ban", "Address", "Warning"
    };

    @Override
    public void beforeValidate(Connection connection) {
        String versionTable = flywayConfiguration.getTable();

        try (PreparedStatement ps = connection.prepareStatement("SELECT sv.checksum FROM schema_version sv WHERE sv.version = ?")) {
            ps.setString(1, MINIMUM_MIGRATION_VERSION);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                // Flyway schema exists, but there's no version 1.1 installed. This is fine!
                return;
            }

            // Here, we know that v1.1 was installed. Therefore we should expect the tables to all be in place.
            for (String requiredTable : REQUIRED_TABLES) {
                try (PreparedStatement check = connection.prepareStatement("SELECT 1 FROM " + requiredTable)) {
                    check.execute();
                } catch (SQLException e) {
                    String message = e.getMessage();
                    if (message.contains("doesn't exist") && message.contains(requiredTable)) {
                        throw new SchemaBrokenException(
                                "It appears the MaxBans database has been tampered with or improperly uninstalled. " +
                                "The table " + requiredTable + " is missing from the database, but the " + versionTable +
                                " (schema_version) table exists. Deleting the " + versionTable + " table may fix this. " +
                                "Or it'll make your problem worse.", e);
                    }

                    // Something else went wrong
                    throw new FlywayException("Unexpected SQLException while verifying database integrity", e);
                }
            }

        } catch (SQLException e) {
            String message = e.getMessage();
            if (message.contains("doesn't exist") && message.contains(versionTable)) {
                // There's no schema_version table, this is fine. Just means we're a fresh install
                return;
            }
            throw new FlywayException("Could not verify " + versionTable + " (schema_version) table exists", e);
        }
    }
}
