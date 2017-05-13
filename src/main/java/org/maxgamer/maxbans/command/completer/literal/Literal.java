package org.maxgamer.maxbans.command.completer.literal;

import org.maxgamer.maxbans.command.completer.token.Token;

/**
 * @author netherfoam
 */
public abstract class Literal<T> {
    private Token token;

    public Literal(Token token) {
        this.token = token;
    }

    public abstract T parse();
}
