package org.maxgamer.maxbans.integration;

import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.DockerPort;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.util.FlywayUtil;

public abstract class AbstractMigrationTest implements IntegrationTest {
    protected JdbcConfig jdbc;
    protected SessionFactory sessionFactory;
    protected Flyway flyway;

    @Before
    public void setupJdbc() {
        jdbc = new JdbcConfig();
        jdbc.setDriver("com.mysql.jdbc.Driver");
        jdbc.setUsername("root");
        jdbc.setPassword("");
    }

    public void doConfigure(Container mysql) {
        DockerPort port = mysql.port(3306);
        String url = port.inFormat("jdbc:mysql://$HOST:$EXTERNAL_PORT/maxbansplus?verifyServerCertificate=false&useSSL=false");

        jdbc.setUrl(url);
        Configuration hibernate = HibernateConfigurer.configuration(jdbc);
        sessionFactory = hibernate.buildSessionFactory();

        flyway = FlywayUtil.migrater(jdbc);
    }
}
