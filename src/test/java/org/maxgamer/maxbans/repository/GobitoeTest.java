package org.maxgamer.maxbans.repository;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
public class GobitoeTest implements IntegrationTest {
    private JdbcConfig jdbc;

    private File storage =  new File("src/test/resources/gobitoe.mv.db");
    private Flyway flyway;

    @Before
    public void init() throws IOException, InterruptedException {
        Assert.assertTrue("Expect " + storage.getAbsolutePath() + " to exist", storage.exists());

        jdbc = new JdbcConfig();
        jdbc.setUrl("jdbc:h2:" + storage.getAbsolutePath());
        jdbc.setDriver("org.h2.Driver");
        jdbc.setUsername("root");
        jdbc.setPassword("password");

        flyway = FlywayUtil.migrater(jdbc);
        flyway.migrate();
    }

    @Test
    public void run() {
        // Okay!
    }

    public JdbcConfig getJdbc() {
        return jdbc;
    }
}
