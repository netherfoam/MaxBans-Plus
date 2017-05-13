package org.maxgamer.maxbans.command.completer.token;

/**
 * @author netherfoam
 */
public class WordToken extends Token<String> {
    public WordToken(int start, int end, char[] data) {
        super(start, end, data);
    }

    @Override
    public String get() {
        return toString();
    }
}
