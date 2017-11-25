package org.maxgamer.maxbans.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.*;

public class TemporalDuration implements TemporalAccessor {
    private static final Temporal BASE_TEMPORAL = LocalDateTime.of(0, 1, 1, 0, 0);
    private static final DateTimeFormatter dtf = new DateTimeFormatterBuilder()
            .optionalStart().appendValue(ChronoField.YEAR).appendLiteral(" Years ").optionalEnd()
            .optionalStart().appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral(" Months ").optionalEnd()
            .optionalStart().appendValue(ChronoField.DAY_OF_MONTH).appendLiteral(" Days ").optionalEnd()
            .optionalStart().appendValue(ChronoField.HOUR_OF_DAY).appendLiteral(" Hours ").optionalEnd()
            .optionalStart().appendValue(ChronoField.MINUTE_OF_HOUR).appendLiteral(" Minutes ").optionalEnd()
            .optionalStart().appendValue(ChronoField.SECOND_OF_MINUTE).appendLiteral(" Seconds ").optionalEnd()
            .toFormatter();
    
    public static TemporalDuration of(Duration d) {
        if(d == null) return null;
        
        return new TemporalDuration(d);
    }
    
    private Duration duration;

    private Temporal temporal;

    public TemporalDuration(Duration duration) {
        if(duration == null) return;
        
        this.duration = duration;
        this.temporal = duration.addTo(BASE_TEMPORAL);
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (!temporal.isSupported(field)) return false;
        long value = temporal.getLong(field) - BASE_TEMPORAL.getLong(field);
        
        return value != 0L;
    }

    @Override
    public long getLong(TemporalField field) {
        if (!isSupported(field)) throw new UnsupportedTemporalTypeException(field.toString());
        
        return temporal.getLong(field) - BASE_TEMPORAL.getLong(field);
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        if(duration == null) return "Never";
        
        return dtf.format(this).trim();
    }

}
