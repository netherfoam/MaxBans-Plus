package org.maxgamer.maxbans.command.completer.tokenizer;

import org.maxgamer.maxbans.command.completer.token.Token;

import java.util.Arrays;
import java.util.List;

/**
 * @author netherfoam
 */
public class Tokenizer {
    private List<Scanner> scanners;
    private char[] data;
    private int pos = 0;

    public Tokenizer(String string, Scanner... scanners) {
        data = string.toCharArray();
        this.scanners = Arrays.asList(scanners);
    }

    public Token read() {
        for(Scanner scanner : scanners) {
            Token token = scanner.read(data, pos);
            if(token != null) {
                pos = token.getEnd();
                return token;
            }
        }

        throw new IllegalArgumentException("Couldn't parse token at pos: " + pos + ": " + String.valueOf(data, pos, 20));
    }
}
