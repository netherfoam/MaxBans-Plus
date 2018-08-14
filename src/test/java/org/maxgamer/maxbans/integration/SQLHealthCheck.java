package org.maxgamer.maxbans.integration;

import com.palantir.docker.compose.connection.Container;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthCheck;
import com.palantir.docker.compose.connection.waiting.SuccessOrFailure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for checking whether a SQL server is accepting connections
 */
public class SQLHealthCheck {
    /**
     * Returns the state of the SQL connection on the given port / driver / database
     *
     * @param port the port
     * @param type the driver eg "mysql"
     * @param database the database eg "dms"
     * @return the resulting health check
     */
    public static HealthCheck<Container> isAvailable(int port, String type, String database, String options) {
        return (target) -> {
            DockerPort dockerPort = target.port(port);

            try {
                String url = "jdbc:" + type + "://" + dockerPort.getIp() + ":" + dockerPort.getExternalPort() + "/" + database + options;
                try (Connection ignored = DriverManager.getConnection(url)) {
                    // // We made a connection without requiring a user: Success!
                    return SuccessOrFailure.success();
                } catch (SQLException e) {
                    // Something went wrong connecting
                    String sqlState = e.getSQLState();

                    if ("28000".equals(sqlState)) {
                        // This means that the authorisation failed. So MySQL is ready for connections, but requires a user
                        // ftp://ftp.software.ibm.com/ps/products/db2/info/vr6/htm/db2m0/db2stt28.htm#TBLCODE28
                        return SuccessOrFailure.success();
                    }

                    return SuccessOrFailure.failure(e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            } finally {
                try {
                    // We give a 2 second break for MySQL to initialise
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // Ignored
                }
            }
        };
    }
}
