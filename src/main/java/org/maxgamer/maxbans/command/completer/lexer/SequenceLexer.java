package org.maxgamer.maxbans.command.completer.lexer;

import org.maxgamer.maxbans.command.completer.token.Token;
import org.maxgamer.maxbans.command.completer.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author netherfoam
 */
public class SequenceLexer implements Lexer {
    private List<Tokenizer<? extends T>> sequence;

    public SequenceLexer(List<Tokenizer<? extends T>> sequence) {
        this.sequence = sequence;
    }

    @Override
    public List<? extends T> parse(String string) {
        char[] data = string.toCharArray();
        List<? extends T> tokens = new ArrayList<>();

        int start = 0;
        for(Tokenizer<?> tokenizer : sequence) {
            Token token = tokenizer.read(data, start);

            if(token == null) throw new IllegalArgumentException("Failed to read " + tokenizer + " at " + start);
        }

        return tokens;
    }
}
