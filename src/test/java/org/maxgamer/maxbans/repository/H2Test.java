package org.maxgamer.maxbans.repository;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.util.FlywayUtil;

import java.io.File;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class H2Test implements IntegrationTest {
    private static boolean initialized = false;
    private static JdbcConfig jdbc;
    
    @Before
    public void init() {
        if(initialized) return;
        
        File f = new File("test-storage.mv.db");
        if(f.exists()) f.delete();
        
        jdbc = new JdbcConfig();
        jdbc.setUrl("jdbc:h2:./test-storage");
        jdbc.setDriver("org.h2.Driver");
        jdbc.setUsername("root");
        jdbc.setPassword("password");

        Flyway flyway = FlywayUtil.migrater(jdbc);
        flyway.migrate();
        
        initialized = true;
    }
    
    public JdbcConfig getJdbc() {
        return jdbc;
    }
}
