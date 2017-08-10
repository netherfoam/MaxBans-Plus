package org.maxgamer.maxbans.util;

import io.sentry.SentryClient;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;
import org.maxgamer.maxbans.MaxBansPlus;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Prints log messages to a delegated plugin logger and also forwards messages above a threshold to sentry
 *
 * @author netherfoam
 */
public class SentryLogger extends Logger {
    /**
     * The name of the java version property we send up
     */
    private static final String JRE_PROPERTY = "Java-Version";

    /**
     * The mapping of java log level to sentry log levels
     */
    private static final HashMap<Level, Event.Level> levels;

    static {
        levels = new HashMap<>(9);

        levels.put(Level.ALL, Event.Level.DEBUG);
        levels.put(Level.FINEST, Event.Level.DEBUG);
        levels.put(Level.FINER, Event.Level.DEBUG);
        levels.put(Level.FINE, Event.Level.DEBUG);
        levels.put(Level.INFO, Event.Level.INFO);
        levels.put(Level.CONFIG, Event.Level.INFO);
        levels.put(Level.WARNING, Event.Level.WARNING);
        levels.put(Level.SEVERE, Event.Level.ERROR);
        levels.put(Level.OFF, null);
    }

    private Logger delegate;

    private String platform;

    private String release;

    private String serverName;

    /**
     * The minimum event level to qualify for logging to sentry
     */
    private Event.Level minimum;

    private SentryClient sentry;

    /**
     * Constructs a new Sentry Logger
     *
     * @param plugin the plugin that is being logged for
     * @param minimum the minimum log level to report, inclusive
     */
    public SentryLogger(MaxBansPlus plugin, Event.Level minimum, SentryClient sentry) {
        this(plugin.getLogger(), plugin.getName(), plugin.getServer().getName() + " " + plugin.getServer().getVersion(),
                plugin.getDescription().getName() + " " + plugin.getDescription().getVersion(),
                plugin.getServer().getServerName(), minimum, sentry);
    }

    /**
     * Constructs a new Sentry Logger
     *
     * @param logName the name of the log eg MaxBans
     * @param platform the bukkit version and implementation
     * @param release the plugin name and version
     * @param serverName the configured server name
     * @param minimum the minimum level to send logs to sentry
     * @param sentry the sentry client
     */
    public SentryLogger(Logger delegate, String logName, String platform, String release, String serverName, Event.Level minimum, SentryClient sentry) {
        super(logName, null);

        this.delegate = delegate;
        this.platform = platform;
        this.release = release;
        this.serverName = serverName;
        this.minimum = minimum;
        this.sentry = sentry;
    }

    @Override
    public void log(LogRecord record) {
        if(delegate != null) {
            delegate.log(record);
        }

        Event.Level level = levels.get(record.getLevel());

        // We don't log this level to Sentry
        if(level == null) return;

        // Because our enum is declared in descending order, this seems a little contradictory. It's correct, because
        // INFO.ordinal() > FATAL.ordinal()
        if(level.ordinal() > minimum.ordinal()) return;

        EventBuilder builder = new EventBuilder()
                .withMessage(record.getMessage())
                .withLevel(level);

        if(record.getThrown() != null) {
            builder.withSentryInterface(new ExceptionInterface(record.getThrown()));
        }

        builder.withServerName(serverName);
        builder.withPlatform(platform);
        builder.withRelease(release);
        builder.withTag(JRE_PROPERTY, System.getProperty("java.version"));

        sentry.sendEvent(builder.build());
    }
}
