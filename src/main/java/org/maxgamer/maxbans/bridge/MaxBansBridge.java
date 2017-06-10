package org.maxgamer.maxbans.bridge;

import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.User;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

/**
 * @author netherfoam
 */
public class MaxBansBridge implements Bridge {
    private File sqliteFile;

    public MaxBansBridge(File sqliteFile) {
        this.sqliteFile = sqliteFile;
    }

    protected Connection getConnection() throws SQLException, ClassNotFoundException {
        // Initialize the SQLite driver class
        Class.forName("org.sqlite.JDBC");

        return DriverManager.getConnection("jdbc:sqlite:" + this.sqliteFile);
    }

    @Override
    public void pull(PluginContext into) throws Exception {
        Connection connection = getConnection();

        try {
            pullBans(into, connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            pullMutes(into, connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pullBans(PluginContext ctx, Connection connection) throws SQLException {
        ResultSet rs = connection.prepareStatement("SELECT * FROM bans").executeQuery();

        while(rs.next()) {
            String name = rs.getString("name");
            String reason = rs.getString("reason");
            String banner = rs.getString("banner");
            long expires = rs.getInt("expires");

            User user = ctx.getLocatorService().user(name);
            User source = null;
            if(banner != null && !banner.isEmpty()) {
                source = ctx.getLocatorService().user(banner);
            }

            Duration duration = null;

            if(expires != 0) {
                long remaining = expires - System.currentTimeMillis();
                if (remaining <= 0) {
                    // Ban is already ineffective
                    continue;
                }

                duration = Duration.ofMillis(remaining);
            }

            try {
                ctx.getUserService().ban(source, user, reason, duration);
            } catch (RejectedException e) {
                System.out.println(e.getMessage());
            }
        }

        rs.close();
    }

    private void pullMutes(PluginContext ctx, Connection connection) throws SQLException {
        ResultSet rs = connection.prepareStatement("SELECT * FROM mutes").executeQuery();

        while(rs.next()) {
            String name = rs.getString("name");
            String reason = rs.getString("reason");
            String banner = rs.getString("muter");
            long expires = rs.getInt("expires");

            User user = ctx.getLocatorService().user(name);
            User source = null;
            if(banner != null && !banner.isEmpty()) {
                source = ctx.getLocatorService().user(banner);
            }

            Duration duration = null;

            if(expires != 0) {
                long remaining = expires - System.currentTimeMillis();
                if (remaining <= 0) {
                    // Ban is already ineffective
                    continue;
                }

                duration = Duration.ofMillis(remaining);
            }

            try {
                ctx.getUserService().mute(source, user, reason, duration);
            } catch (RejectedException e) {
                System.out.println(e.getMessage());
            }
        }

        rs.close();
    }
}
