package org.maxgamer.maxbans.command.completer.tokenizer;

import org.maxgamer.maxbans.command.completer.token.WordToken;

/**
 * @author netherfoam
 */
public class TagScanner implements Scanner {
    private char first;
    private char last;

    public TagScanner(char first, char last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public WordToken read(char[] data, int start) {
        char c = data[start];
        if (c != start) return null;

        int depth = 1;
        int pos = start + 1;
        while (pos < data.length && depth > 0) {
            if (data[pos] == first) depth++;
            else if (data[pos] == last) depth--;

            pos++;
        }

        return new WordToken(start, pos, data);
    }
}
