package org.maxgamer.maxbans.orm;

import org.hibernate.cfg.Configuration;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.orm.id.UserAddressId;

/**
 * @author Dirk Jamieson
 */
public class HibernateConfigurer {
    public static Configuration configuration(JdbcConfig jdbc) {
        Configuration config = new Configuration();

        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(Address.class);
        config.addAnnotatedClass(Ban.class);
        config.addAnnotatedClass(Mute.class);
        config.addAnnotatedClass(UserAddress.class);
        config.addAnnotatedClass(UserAddressId.class);
        config.addAnnotatedClass(Warning.class);

        config.setProperty("hibernate.connection.driver_class", jdbc.getDriver());
        config.setProperty("hibernate.connection.url", jdbc.getUrl());
        config.setProperty("hibernate.connection.username", jdbc.getUsername());
        config.setProperty("hibernate.connection.password", jdbc.getPassword());
        config.setProperty("hibernate.show_sql", "true");

        return config;
    }

    private HibernateConfigurer() {
        throw new RuntimeException("Not implemented");
    }
}
