package org.maxgamer.maxbans.util;

import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.Restriction;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class RestrictionUtil {
    public static <R extends Restriction> void assertRestrictionLonger(R existing, R replacement) throws RejectedException {
        if(existing == null) return;
        if(existing.getExpiresAt() == null) {
            // The old restriction lasts forever
            throw new RejectedException("Existing restriction lasts longer");
        }
        
        if(replacement.getExpiresAt() == null) {
            // The new restriction lasts forever and is replacing a temporary one
            return;
        }
        
        if(existing.getExpiresAt().isAfter(replacement.getExpiresAt())) {
            throw new RejectedException("Existing restriction lasts longer");
        }
    }
    
    public static <R extends Restriction> void assertRestrictionLonger(Iterable<R> existingRestrictions, R replacement) throws RejectedException {
        for(R existing : existingRestrictions) {
            assertRestrictionLonger(existing, replacement);
        }
    }
    
    public static boolean isActive(Restriction r) {
        if(r == null) return false;
        if(r.getExpiresAt() == null) return true;
        
        return r.getExpiresAt().isAfter(Instant.now());
    }
    
    public static boolean isActive(Iterable<? extends Restriction> restrictions) {
        for(Restriction r : restrictions) {
            if(isActive(r)) return true;
        }
        
        return false;
    }
    
    public static Duration getDuration(LinkedList<String> args) {
        if(args.size() < 2) return null;
        
        try {
            int value = Integer.valueOf(args.get(0));
            
            String unitName = args.get(1);
            
            for(TimeUnit unit : TimeUnit.values()) {
                if(unit.name().toLowerCase().startsWith(unitName.toLowerCase())) {
                    args.pop();
                    args.pop();
                    return Duration.ofMillis(unit.toMillis(value));
                }
            }
            
            // Unit couldn't be parsed
            return null;
        } catch (NumberFormatException e) {
            // No value specified
            return null;
        }
    }
    
    private RestrictionUtil() {
        throw new RuntimeException("No constructor");
    }
}
