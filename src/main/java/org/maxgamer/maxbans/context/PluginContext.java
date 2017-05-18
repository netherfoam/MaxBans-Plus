package org.maxgamer.maxbans.context;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.config.WarningConfig;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.repository.*;
import org.maxgamer.maxbans.service.*;
import org.maxgamer.maxbans.transaction.Transactor;

import java.io.File;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginContext {
    private PluginConfig config;
    private Server server;
    private File dataFolder;

    private SessionFactory sessionFactory;
    private Transactor transactor;
    private UserRepository userRepository;
    private BanRepository banRepository;
    private MuteRepository muteRepository;
    private AddressRepository addressRepository;
    private WarningRepository warningRepository;

    private UserService userService;
    private BroadcastService broadcastService;
    private LocatorService locatorService;
    private AddressService addressService;
    private WarningService warningService;
    private LockdownService lockdownService;
    private GeoIPService geoIPService;

    private MetricService metricService;
    
    public PluginContext(PluginConfig config, Server server, File dataFolder, MetricService metrics) {
        this.config = config;
        this.server = server;
        this.dataFolder = dataFolder;
        this.metricService = metrics;

        JdbcConfig jdbc = config.getJdbcConfig();
        WarningConfig warnings = config.getWarningConfig();
        Configuration hibernate = HibernateConfigurer.configuration(jdbc);
        FileConfiguration lockdownConfig = YamlConfiguration.loadConfiguration(new File(dataFolder, "lockdown.yml"));

        sessionFactory = hibernate.buildSessionFactory();
        transactor = new Transactor(sessionFactory);

        userRepository = new UserRepository(transactor);
        banRepository = new BanRepository(transactor);
        muteRepository = new MuteRepository(transactor);
        addressRepository = new AddressRepository(transactor);
        warningRepository = new WarningRepository(transactor);

        // TODO: Tests can be sped up by not doing this or having a smaller database
        geoIPService = new GeoIPService(getClass().getClassLoader().getResourceAsStream("GeoLite.zip"), "en");
        broadcastService = new BroadcastService(server);
        userService = new UserService(config, userRepository, banRepository, muteRepository);
        locatorService = new LocatorService(server, userService);
        addressService = new AddressService(banRepository, muteRepository, addressRepository, geoIPService);
        warningService = new WarningService(server, warningRepository, locatorService, warnings);
        lockdownService = new LockdownService(server, userService, broadcastService, lockdownConfig);
    }

    public void close() {
        sessionFactory.close();
    }

    public PluginConfig getConfig() {
        return config;
    }

    public Server getServer() {
        return server;
    }

    public File getDataFolder() {
        return dataFolder;
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

    public AddressRepository getAddressRepository() {
        return addressRepository;
    }

    public AddressService getAddressService() {
        return addressService;
    }

    public WarningService getWarningService() {
        return warningService;
    }

    public WarningRepository getWarningRepository() {
        return warningRepository;
    }

    public LockdownService getLockdownService() {
        return lockdownService;
    }

    public MetricService getMetricService() {
        return metricService;
    }

    public GeoIPService getGeoIPService() {
        return geoIPService;
    }
}
