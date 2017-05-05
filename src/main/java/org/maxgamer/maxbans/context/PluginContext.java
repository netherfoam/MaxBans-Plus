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
import org.maxgamer.maxbans.service.UserService;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginContext {
    private PluginConfig config;

    private SessionFactory sessionFactory;
    private UserRepository userRepository;
    private BanRepository banRepository;
    private MuteRepository muteRepository;
    private UserService userService;
    private BroadcastService broadcastService;
    
    public PluginContext(PluginConfig config, Server server) {
        this.config = config;

        JdbcConfig jdbc = config.getJdbcConfig();
        Configuration hibernate = HibernateConfigurer.configuration(jdbc);

        sessionFactory = hibernate.buildSessionFactory();

        userRepository = new UserRepository(sessionFactory);
        banRepository = new BanRepository(sessionFactory);
        muteRepository = new MuteRepository(sessionFactory);
        broadcastService = new BroadcastService(server);

        userService = new UserService(config, userRepository, banRepository, muteRepository);
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
}
