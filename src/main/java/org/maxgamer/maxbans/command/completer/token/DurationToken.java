package org.maxgamer.maxbans.command.completer.token;

import org.maxgamer.maxbans.command.completer.tokenizer.WordScanner;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author netherfoam
 */
public class DurationToken extends CompletableToken<Duration> {
    private WordScanner word = new WordScanner();
    private int value;
    private TimeUnit unit;

    public DurationToken(int start, int end, char[] data, int value, TimeUnit unit) {
        super(start, end, data);
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Duration get() {
        return Duration.ofMillis(unit.toMicros(value));
    }

    @Override
    public List<String> completions() {
        String text = toString().toLowerCase();

        if(!text.contains(" ")) {
            // First half, it's a number. We can't auto-complete a number for you!
            return Collections.emptyList();
        }

        String[] split = text.split(" ");
        String units = split[1].toLowerCase();

        List<String> completions = new LinkedList<>();
        for(TimeUnit unit : TimeUnit.values()) {
            if(unit.name().toLowerCase().startsWith(units)) {
                completions.add(unit.name().toLowerCase());
            }
        }

        if(completions.isEmpty()) {
            // No values match? How about all of them!
            for(TimeUnit unit : TimeUnit.values()) {
                completions.add(unit.name().toLowerCase());
            }
        }

        return completions;
    }
}
