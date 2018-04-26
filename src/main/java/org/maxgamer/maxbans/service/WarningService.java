package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.config.WarningConfig;
import org.maxgamer.maxbans.event.WarnUserEvent;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.Warning;
import org.maxgamer.maxbans.repository.WarningRepository;
import org.maxgamer.maxbans.util.StringUtil;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author netherfoam
 */
public class WarningService {
    private Server server;
    private WarningRepository repository;
    private LocatorService locatorService;
    private WarningConfig config;
    private EventService eventService;

    @Inject
    public WarningService(Server server, WarningRepository repository, LocatorService locatorService, WarningConfig config, EventService eventService) {
        this.server = server;
        this.repository = repository;
        this.locatorService = locatorService;
        this.config = config;
        this.eventService = eventService;
    }

    public MessageBuilder warn(User source, User user, String reason, Locale locale) throws CancelledException {
        List<Warning> warnings = user.getWarnings();

        Warning warning = new Warning(user);
        warning.setSource(source);
        warning.setExpiresAt(warning.getCreated().plus(config.getDuration()));
        warning.setReason(reason);

        WarnUserEvent event = new WarnUserEvent(source, user, warning);
        eventService.call(event);
        if (event.isCancelled()) {
            throw new CancelledException();
        }

        repository.save(warning);

        int strike = warnings.size() % config.getStrikes() + 1;
        warnings.add(warning);

        List<String> penalties = config.getPenalty(strike);
        if (penalties != null && !penalties.isEmpty()) {
            Map<String, Object> substitutions = new HashMap<>();
            substitutions.put("name", user.getName());
            substitutions.put("source", source);
            substitutions.put("reason", reason);
            substitutions.put("strike", strike);

            for (String penalty : penalties) {
                penalise(penalty, substitutions);
            }
        }

        MessageBuilder message = locale.get()
                .withUserOrConsole("source", source)
                .with("reason", reason)
                .with("duration", config.getDuration())
                .with("name", user);

        Player player = locatorService.player(user);
        if (player != null) {
            player.sendMessage(message.get("warn.warned"));
        }

        return message;
    }

    private void penalise(String penalty, Map<String, Object> substitutions) {
        if (penalty.startsWith("/")) {
            penalty = penalty.substring(1);
        }

        // Expand penalty as if it were a placeholder message
        penalty = StringUtil.expand(penalty, substitutions);

        try {
            server.dispatchCommand(server.getConsoleSender(), penalty);
        } catch (RuntimeException e) {
            server.getLogger().log(Level.WARNING,
                    "Failed to run warning penalty command: '" + penalty + "'. The command threw " +
                            "an exception. Please report this to the author of the command. This is " +
                            "not an issue with MaxBans Plus.", e);
        }
    }
}
