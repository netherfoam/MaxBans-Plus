package org.maxgamer.maxbans.command.completer.tokenizer;

import org.maxgamer.maxbans.command.completer.token.WordToken;

/**
 * @author netherfoam
 */
public class WordScanner implements Scanner {
    @Override
    public WordToken read(char[] data, int start) {
        char c = data[start];

        if (!Character.isLetterOrDigit(c)) return null;

        int pos = start + 1;
        while (Character.isLetterOrDigit(data[pos++])) {
            // Empty
        }

        return new WordToken(start, pos, data);
    }
}
