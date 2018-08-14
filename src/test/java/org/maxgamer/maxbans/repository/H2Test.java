package org.maxgamer.maxbans.repository;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.util.FlywayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class H2Test implements IntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(H2Test.class);

    private JdbcConfig jdbc;

    private File storage =  new File("test-storage.mv.db");
    private Flyway flyway;

    @Before
    public void init() throws IOException, InterruptedException {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        LOGGER.info("Creating database...");

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

        File[] files = new File("").listFiles();
        if (files != null) {
            LOGGER.info("Files here are: " + String.join(", ", Arrays.stream(files).map(File::getName).collect(Collectors.toList())));
        } else {
            LOGGER.info("NO FILES");
        }

        LOGGER.info("JDBC Details: " + jdbc.toString());
        LOGGER.info("Initialising Schema...");
        flyway = FlywayUtil.migrater(jdbc);
        flyway.migrate();
    }

    @After
    public void teardown() throws IOException, InterruptedException {
        DataSource source = flyway.getDataSource();
        try (Connection c = source.getConnection()) {
            c.createStatement().execute("SHUTDOWN");
        } catch (SQLException e) {
            LOGGER.info("Failed to shutdown properly", e);
        }

        LOGGER.info("Tearing down database...");
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 5000) {
            if (storage.delete() || !storage.exists()) {
                LOGGER.info("... Teardown complete!");
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
