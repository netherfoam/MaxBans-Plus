package org.maxgamer.maxbans.locale;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.UserAddress;
import org.maxgamer.maxbans.service.GeoIPService;
import org.maxgamer.maxbans.util.geoip.GeoCountry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * TODO: Document this
 */
public class TooltipMessageBuilder extends MessageBuilder {
    private GeoIPService geoIPService;

    public TooltipMessageBuilder(Locale locale, GeoIPService geoIPService) {
        super(locale);
        this.geoIPService = geoIPService;
    }

    @Override
    public MessageBuilder withUserOrConsole(String key, User user) {
        if (user == null) {
            // Standard behaviour for missing users
            return super.with(key, null);
        }

        return this.with(key, user);
    }

    @Override
    public MessageBuilder with(String key, User user) {
        if (user == null) {
            // Standard behaviour for missing users
            return super.with(key, null);
        }

        BaseComponent component = toComponent(user);

        substitutions.put(key, component);

        return this;
    }

    @Override
    public Message get(String templateId) {
        return new TooltipMessage(locale, substitutions, templateId);
    }

    private BaseComponent toComponent(User user) {
        TextComponent name = new TextComponent(user.getName());
        name.setColor(ChatColor.LIGHT_PURPLE);

        MessageBuilder hover = this
                .with("firstActive", user.getFirstActive())
                .with("lastActive", user.getLastActive());

        UserAddress lastAddress = user.getLastAddress();
        if (lastAddress != null) {
            List<String> alts = new ArrayList<>();

            // Sort by most recent users first
            List<UserAddress> mostRecentUsers = new ArrayList<>(lastAddress.getAddress().getUsers());
            mostRecentUsers.sort(Comparator.comparing(UserAddress::getFirstActive));

            for (UserAddress alt : mostRecentUsers) {
                alts.add(alt.getUser().getName());
            }

            String host = lastAddress.getAddress().getHost();
            GeoCountry country = geoIPService.getCountry(host);

            hover.with("users", alts)
                    .with("ip", host)
                    .with("users", alts);

            if (country != null) {
                hover.with("country", country.getCountryName())
                        .with("continent", country.getContinentName());
            }
        }

        Message text = hover.get("hover.user");

        name.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(text.toString())
        ));

        return name;
    }
}
