package org.maxgamer.maxbans.command.completer.lexer;

import java.util.List;

/**
 * @author netherfoam
 */
public interface Lexer<T> {
    List<T> parse(String string);
}
