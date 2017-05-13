package org.maxgamer.maxbans.command.completer.token;

import java.util.List;

/**
 * @author netherfoam
 */
public abstract class CompletableToken<T> extends Token {
    public CompletableToken(int start, int end, char[] data) {
        super(start, end, data);
    }

    public abstract List<String> completions();
}
