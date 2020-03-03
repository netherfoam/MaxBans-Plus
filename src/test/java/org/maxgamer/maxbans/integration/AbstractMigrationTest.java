package org.maxgamer.maxbans.integration;

import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.DockerPort;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.util.jdbc.DriverDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.util.FlywayUtil;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractMigrationTest implements IntegrationTest {
    protected static final String DATABASE_NAME = "maxbansplus";
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

    public void doConfigure(Container mysql) throws SQLException {
        DockerPort port = mysql.port(3306);
        String url = port.inFormat("jdbc:mysql://$HOST:$EXTERNAL_PORT/" + DATABASE_NAME + "?verifyServerCertificate=false&useSSL=false");

        jdbc.setUrl(url);

        flyway = FlywayUtil.migrater(jdbc);
        DriverDataSource initSource = new DriverDataSource(flyway.getClassLoader(), null, port.inFormat("jdbc:mysql://$HOST:$EXTERNAL_PORT/"), jdbc.getUsername(), jdbc.getPassword());

        try (Connection c = initSource.getConnection()) {
            c.createStatement()
                    .execute("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
        }
        initSource.close();

        Configuration hibernate = HibernateConfigurer.configuration(jdbc);
        sessionFactory = hibernate.buildSessionFactory();
    }
}
