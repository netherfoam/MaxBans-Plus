package org.maxgamer.maxbans.util.db;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.callback.BaseFlywayCallback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Version 1.1 for MySQL didn't work on MySQL 5.7 since it used Timestamp values of 0 instead of now() as defaults.
 * Hence the migration was never applied for some people, but it was applied successfully for others. This is part
 * of the workaround that fixes that issue.
 *
 * It "fixes" the checksum of version 1.1 to the expected value forcibly, before validation, so that no exception is
 * triggered when Flyway would otherwise notice that the migration script has been modified after it was applied.
 */
public class Flyway_MySQL_V1_1__Fix_Callback extends BaseFlywayCallback {
    /**
     * The schema version we want to fix
     */
    public static final String MIGRATION_VERSION = "1.1";

    /**
     * The checksum of the schema version that passes Flyway's checksum test
     */
    private static final long DESIRED_CHECKSUM = -2053758196;

    @Override
    public void beforeValidate(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT sv.checksum FROM schema_version sv WHERE sv.version = ?")) {
            ps.setString(1, MIGRATION_VERSION);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                // Flyway schema exists, but there's no version 1.1 installed. This is fine!
                return;
            }

            if (rs.getLong("sv.checksum") == DESIRED_CHECKSUM) {
                // The checksum currently in Flyway is fine. No changes need to be made.
                return;
            }

            // Repair old script checksum
            try (PreparedStatement fix = connection.prepareStatement("UPDATE schema_version SET checksum = ? WHERE version = ?")) {
                fix.setLong(1, DESIRED_CHECKSUM);
                fix.setString(2, MIGRATION_VERSION);
                fix.execute();
            }
        } catch (SQLException e) {
            String message = e.getMessage();
            if (message.contains("doesn't exist") && message.contains("schema_version")) {
                // There's no schema_version table, this is fine. Just means we're a fresh install
                return;
            }
            throw new FlywayException("Could not verify first migration checksum", e);
        }
    }
}
