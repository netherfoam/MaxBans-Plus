package org.maxgamer.maxbans.util.dialect;

import org.hibernate.dialect.*;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

/**
 * Dialect Resolver that makes Hibernate work with MySQL 8.0
 */
public class MySQL80DialectResolver implements DialectResolver  {
    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        String databaseName = info.getDatabaseName();
        if ("MySQL".equals(databaseName)) {
            int majorVersion = info.getDatabaseMajorVersion();
            if (majorVersion < 8) {
                return null;
            }

            return new MySQL8Dialect();
        } else {
            return null;
        }
    }
}
