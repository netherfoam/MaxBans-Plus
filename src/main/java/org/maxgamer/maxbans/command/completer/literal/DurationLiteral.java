package org.maxgamer.maxbans.command.completer.literal;

import org.maxgamer.maxbans.command.completer.token.Token;
import org.maxgamer.maxbans.command.completer.tokenizer.WordScanner;

import java.time.Duration;

/**
 * @author netherfoam
 */
public class DurationLiteral extends Literal<Duration> {
    private WordScanner unit = new WordScanner();

    public DurationLiteral(Token token) {
        super(token);
    }

    @Override
    public Duration parse() {
       /* IntegerToken v = value.read(data, start);
        if(v == null) return null;

        WordToken u = unit.read(data, v.getStart() + 1);
        if(u == null) return null;

        String prefix = u.get().toUpperCase();
        for(TimeUnit unit : TimeUnit.values()) {
            if(!unit.name().startsWith(prefix)) continue;

            return new DurationToken(start, u.getEnd(), data, v.get(), unit);
        }*/

        // Not a valid time unit
        return null;
    }
}
