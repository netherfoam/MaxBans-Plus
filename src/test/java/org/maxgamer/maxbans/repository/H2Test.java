package org.maxgamer.maxbans.repository;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.util.FlywayUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class H2Test implements IntegrationTest {
    private static JdbcConfig jdbc;

    private File storage =  new File("test-storage.mv.db");

    @Before
    public void init() throws IOException {
        if (!storage.delete() && storage.exists()) {
            throw new IOException("Unable to delete " + storage.getAbsolutePath());
        }

        jdbc = new JdbcConfig();
        jdbc.setUrl("jdbc:h2:./test-storage");
        jdbc.setDriver("org.h2.Driver");
        jdbc.setUsername("root");
        jdbc.setPassword("password");

        Flyway flyway = FlywayUtil.migrater(jdbc);
        flyway.migrate();
    }

    @After
    public void teardown() throws IOException, InterruptedException {
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
