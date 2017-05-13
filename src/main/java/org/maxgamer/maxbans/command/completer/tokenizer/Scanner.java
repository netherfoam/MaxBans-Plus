package org.maxgamer.maxbans.command.completer.tokenizer;

import org.maxgamer.maxbans.command.completer.token.Token;

/**
 * @author netherfoam
 */
public interface Scanner {
    Token read(char[] data, int start);
}
