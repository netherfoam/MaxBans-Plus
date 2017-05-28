package org.maxgamer.maxbans.repository;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.util.FlywayUtil;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class MigrationTest implements IntegrationTest {
    private JdbcConfig jdbc;
    private SessionFactory sessionFactory;
    private Flyway flyway;
    
    @Before
    public void init() {
        jdbc = new JdbcConfig();
        jdbc.setUrl("jdbc:h2:mem:");
        jdbc.setDriver("org.h2.Driver");
        jdbc.setUsername("root");
        jdbc.setPassword("password");

        Configuration hibernate = HibernateConfigurer.configuration(jdbc);
        sessionFactory = hibernate.buildSessionFactory();

        flyway = FlywayUtil.migrater(jdbc);
    }
    
    @Test
    public void test() {
        flyway.migrate();
    }
}
