package org.maxgamer.maxbans.command.completer.token;

/**
 * @author netherfoam
 */
public abstract class Token<T> {
    protected final int start;
    protected final int end;
    protected final char[] data;

    public Token(int start, int end, char[] data) {
        this.start = start;
        this.end = end;
        this.data = data;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String toString() {
        return String.valueOf(data, start, end);
    }

    public abstract T get();
}
