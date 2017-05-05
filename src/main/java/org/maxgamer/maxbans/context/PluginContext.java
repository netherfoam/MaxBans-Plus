package org.maxgamer.maxbans.context;

import org.bukkit.Server;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.repository.BanRepository;
import org.maxgamer.maxbans.repository.MuteRepository;
import org.maxgamer.maxbans.repository.UserRepository;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.transaction.Transactor;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginContext {
    private PluginConfig config;

    private SessionFactory sessionFactory;
    private Transactor transactor;
    private UserRepository userRepository;
    private BanRepository banRepository;
    private MuteRepository muteRepository;
    private UserService userService;
    private BroadcastService broadcastService;
    private LocatorService locatorService;
    
    public PluginContext(PluginConfig config, Server server) {
        this.config = config;

        JdbcConfig jdbc = config.getJdbcConfig();
        Configuration hibernate = HibernateConfigurer.configuration(jdbc);

        sessionFactory = hibernate.buildSessionFactory();
        transactor = new Transactor(sessionFactory);

        userRepository = new UserRepository(transactor);
        banRepository = new BanRepository(transactor);
        muteRepository = new MuteRepository(transactor);

        broadcastService = new BroadcastService(server);
        userService = new UserService(config, userRepository, banRepository, muteRepository);
        locatorService = new LocatorService(server, userService);
    }

    public PluginConfig getConfig() {
        return config;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public BanRepository getBanRepository() {
        return banRepository;
    }

    public MuteRepository getMuteRepository() {
        return muteRepository;
    }

    public UserService getUserService() {
        return userService;
    }

    public BroadcastService getBroadcastService() {
        return broadcastService;
    }

    public Transactor getTransactor() {
        return transactor;
    }

    public LocatorService getLocatorService() {
        return locatorService;
    }
}
