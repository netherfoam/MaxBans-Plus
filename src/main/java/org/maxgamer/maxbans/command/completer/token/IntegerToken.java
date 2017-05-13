package org.maxgamer.maxbans.command.completer.token;

/**
 * @author netherfoam
 */
public class IntegerToken extends Token<Integer> {
    private int value;

    public IntegerToken(int start, int end, char[] data) {
        super(start, end, data);

        value = Integer.parseInt(String.valueOf(data, start, end));
    }

    @Override
    public Integer get() {
        return value;
    }
}
