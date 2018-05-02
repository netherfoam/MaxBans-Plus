package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.event.KickUserEvent;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.EventService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.service.metric.MetricService;
import org.maxgamer.maxbans.util.RestrictionUtil;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author netherfoam
 */
public class KickCommandExecutor extends StandardCommandExecutor {
    @Inject
    protected LocatorService locatorService;

    @Inject
    protected BroadcastService broadcastService;

    @Inject
    protected MetricService metricService;

    @Inject
    protected EventService eventService;

    @Inject
    public KickCommandExecutor() {
        super("maxbans.kick");
    }

    @Override
    public void perform(CommandSender sender, Command command, String s, String[] userArgs) throws CancelledException {
        LinkedList<String> args = new LinkedList<>(Arrays.asList(userArgs));
        boolean silent = RestrictionUtil.isSilent(args);

        if(args.size() <= 0) {
            sender.sendMessage("Must supply target name");
            return;
        }

        Player player = locatorService.player(args.pop());
        if(player == null) {
            sender.sendMessage("Player not found");
            return;
        }

        KickUserEvent event = new KickUserEvent(sender, player);
        eventService.call(event);
        if (event.isCancelled()) {
            throw new CancelledException();
        }

        String reason = String.join(" ", args);
        MessageBuilder properties = locale.get()
                .with("source", sender instanceof Player ? sender.getName() : "Console")
                .with("name", player.getName())
                .with("reason", reason);

        player.kickPlayer(properties.get("kick.message").toString());

        broadcastService.broadcast(properties.get("kick.broadcast"), silent, sender);
        metricService.increment(MetricService.KICKS);
    }
}
