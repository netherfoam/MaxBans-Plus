package org.maxgamer.maxbans.util;

import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.*;

/**
 * Provides a way of injecting information about a user, address or restrictions into a MessageBuilder in bulk
 */
public class MessageUtil {
    public static MessageBuilder inject(MessageBuilder builder, Tenant tenant) {
        if (tenant instanceof User) {
            return inject(builder, (User) tenant);
        }

        if (tenant instanceof Address) {
            return inject(builder, (Address) tenant);
        }

        throw new IllegalArgumentException("Unexpected Tenant type: " + tenant);
    }
    
    public static MessageBuilder inject(MessageBuilder builder, User user) {
        builder.with("id", user.getId());
        builder.withUserOrConsole("name", user);

        // Check last address
        UserAddress lastAddress = user.getLastAddress();
        if (lastAddress != null) {
            builder.with("address", lastAddress.getAddress().getHost());
        }

        return builder;
    }

    public static MessageBuilder inject(MessageBuilder builder, Address address) {
        builder.with("address", address.getHost());

        return builder;
    }

    public static MessageBuilder inject(MessageBuilder builder, Restriction restriction) {
        builder.with("reason", restriction.getReason());
        builder.withUserOrConsole("source", restriction.getSource());
        builder.with("duration", restriction.getExpiresAt());
        builder.with("created", restriction.getCreated());

        return builder;
    }

    private MessageUtil() {
        // Utility class is static
    }
}
