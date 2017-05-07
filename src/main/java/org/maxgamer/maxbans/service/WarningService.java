package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.config.WarningConfig;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.Warning;
import org.maxgamer.maxbans.repository.WarningRepository;
import org.maxgamer.maxbans.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author netherfoam
 */
public class WarningService {
    private Server server;
    private WarningRepository repository;
    private BroadcastService broadcastService;
    private LocatorService locatorService;
    private WarningConfig config;

    public WarningService(Server server, WarningRepository repository, BroadcastService broadcastService, LocatorService locatorService, WarningConfig config) {
        this.server = server;
        this.repository = repository;
        this.broadcastService = broadcastService;
        this.locatorService = locatorService;
        this.config = config;
    }

    public void warn(User source, User user, String reason, Locale locale) {
        List<Warning> warnings = user.getWarnings();

        Warning warning = new Warning(user);
        warning.setSource(source);
        warning.setExpiresAt(warning.getCreated().plus(config.getDuration()));
        warning.setReason(reason);

        repository.save(warning);

        warnings.add(warning);

        int strike = warnings.size() % config.getStrikes();
        String penalty = config.getPenalty(strike);
        if(penalty == null) return;

        if(penalty.startsWith("/")) {
            penalty = penalty.substring(1);
        }

        Map<String, Object> substitutions = new HashMap<>();
        substitutions.put("name", user.getName());
        substitutions.put("source", source == null ? null : source.getName());
        substitutions.put("reason", reason);
        substitutions.put("strike", strike);

        penalty = StringUtil.expand(penalty, substitutions);

        // TODO: Expand penalty as if it were a placeholder message
        server.dispatchCommand(server.getConsoleSender(), penalty);

        Locale.MessageBuilder builder = locale.get()
                .with("source", source == null ? null : source.getName())
                .with("reason", reason)
                .with("duration", config.getDuration())
                .with("name", user.getName());

        broadcastService.broadcast(builder.get("warn.broadcast"), false);
        Player player = locatorService.player(user);
        if(player != null) {
            player.sendMessage(builder.get("warn.warned"));
        }
    }
}
