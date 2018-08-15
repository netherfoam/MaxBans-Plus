package org.maxgamer.maxbans.repository;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.util.FlywayUtil;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class H2Test implements IntegrationTest {
    private JdbcConfig jdbc;

    private File storage =  new File("test-storage.mv.db");
    private Flyway flyway;

    @Before
    public void init() throws IOException, InterruptedException {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        java.util.logging.Logger.getLogger("org.flywaydb.core.internal").setLevel(Level.WARNING);

        if (!storage.delete() && storage.exists()) {
            throw new IOException("Unable to delete " + storage.getAbsolutePath());
        }

        if (storage.exists()) {
            throw new IOException("but.. storage exists somehow?");
        }

        jdbc = new JdbcConfig();
        jdbc.setUrl("jdbc:h2:./test-storage");
        jdbc.setDriver("org.h2.Driver");
        jdbc.setUsername("root");
        jdbc.setPassword("password");

        flyway = FlywayUtil.migrater(jdbc);
        flyway.migrate();
    }

    @After
    public void teardown() throws IOException, InterruptedException, SQLException {
        DataSource source = flyway.getDataSource();
        try (Connection c = source.getConnection()) {
            c.createStatement().execute("SHUTDOWN");
        }

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 5000) {
            if (storage.delete() || !storage.exists()) {
                return;
            }
            Thread.sleep(1);
        }

        throw new IOException("Unable to delete " + storage.getAbsolutePath() + " after " + (System.currentTimeMillis() - start) + "ms");
    }

    public JdbcConfig getJdbc() {
        return jdbc;
    }
}
