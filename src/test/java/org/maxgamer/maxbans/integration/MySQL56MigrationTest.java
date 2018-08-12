package org.maxgamer.maxbans.integration;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.test.IntegrationTest;

public class MySQL56MigrationTest extends AbstractMigrationTest implements IntegrationTest {
    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/docker/mysql-56.yml")
            .waitingForService("mysql", HealthChecks.toHaveAllPortsOpen())
            .build();

    @Before
    public void setup() {
        Container mysql = docker.containers().container("mysql");
        doConfigure(mysql);
    }

    @Test
    public void test() {
        flyway.migrate();
    }
}
