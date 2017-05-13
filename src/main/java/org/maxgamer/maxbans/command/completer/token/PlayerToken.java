package org.maxgamer.maxbans.command.completer.token;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.command.completer.tokenizer.WordScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author netherfoam
 */
public class PlayerToken extends CompletableToken<Player> {
    private Server server;
    private WordScanner word = new WordScanner();

    public PlayerToken(int start, int end, char[] data, Server server) {
        super(start, end, data);
        this.server = server;
    }

    @Override
    public List<String> completions() {
        List<String> names = new ArrayList<>();

        Player recommended = server.getPlayer(toString());
        if(recommended != null) names.add(recommended.getName());

        String lower = toString().toLowerCase();

        for(Player player : server.getOnlinePlayers()) {
            String name = player.getName();
            if(name.toLowerCase().startsWith(lower)) {
                names.add(name);
            }
        }

        return names;
    }
}
