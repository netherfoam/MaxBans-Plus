package org.maxgamer.maxbans.command.completer;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.maxgamer.maxbans.command.completer.token.Token;
import org.maxgamer.maxbans.command.completer.tokenizer.TagTokenizer;
import org.maxgamer.maxbans.command.completer.tokenizer.Tokenizer;
import org.maxgamer.maxbans.command.completer.lexer.UsageLexer;
import org.maxgamer.maxbans.command.completer.tokenizer.WordScanner;
import org.maxgamer.maxbans.service.LocatorService;

import java.util.Arrays;
import java.util.List;

/**
 * @author netherfoam
 */
public class UsageCompleter implements TabCompleter {
    private static final List<Tokenizer<?>> USAGE_TOKENIZERS = Arrays.asList(
            new TagTokenizer('<', '>'),
            new TagTokenizer('[', ']'),
            new WordScanner()
    );

    private Server server;
    private LocatorService locatorService;

    private UsageLexer usageLexer = new UsageLexer(USAGE_TOKENIZERS);

    public UsageCompleter(LocatorService locatorService, Server server) {
        this.locatorService = locatorService;
        this.server = server;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String name, String[] userArgs) {
        String usage = command.getUsage();

        List<Token> usageTokens = usageLexer.parse(usage);

        // The first token is the command name, but the args don't contain it
        usageTokens.remove(0);


    }
}
