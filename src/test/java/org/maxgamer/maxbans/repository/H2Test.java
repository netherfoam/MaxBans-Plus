package org.maxgamer.maxbans.repository;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.maxgamer.maxbans.config.JdbcConfig;

import java.io.File;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class H2Test {
    private static boolean initialized = false;
    private JdbcConfig jdbc;
    
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

        Flyway flyway = new Flyway();
        flyway.setClassLoader(getClass().getClassLoader());
        flyway.setDataSource(jdbc.getUrl(), jdbc.getUsername(), jdbc.getPassword());
        flyway.migrate();
        
        initialized = true;
    }
    
    public JdbcConfig getJdbc() {
        return jdbc;
    }
}
