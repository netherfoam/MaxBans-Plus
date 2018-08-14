package org.maxgamer.maxbans.integration;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.sql.SQLException;

public class MySQL57MigrationTest extends AbstractMigrationTest implements IntegrationTest {
    @ClassRule
    public static DockerComposeRule docker = DockerComposeRule.builder()
            .file("src/test/docker/mysql-57.yml")
            .waitingForService("mysql", HealthChecks.toHaveAllPortsOpen())
            .waitingForService("mysql", SQLHealthCheck.isAvailable(3306, "mysql", DATABASE_NAME, "?verifyServerCertificate=false&useSSL=false"))
            .build();

    @Before
    public void setup() throws SQLException {
        Container mysql = docker.containers().container("mysql");
        doConfigure(mysql);
    }

    @Test
    public void test() {
        flyway.migrate();
    }
}
