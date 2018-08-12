package org.maxgamer.maxbans.service;

import org.bukkit.entity.Player;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.event.BanUserEvent;
import org.maxgamer.maxbans.event.MuteUserEvent;
import org.maxgamer.maxbans.event.UnbanUserEvent;
import org.maxgamer.maxbans.event.UnmuteUserEvent;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.UserAddress;
import org.maxgamer.maxbans.repository.BanRepository;
import org.maxgamer.maxbans.repository.MuteRepository;
import org.maxgamer.maxbans.repository.UserRepository;
import org.maxgamer.maxbans.util.RestrictionUtil;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class UserService {
    private PluginConfig config;
    private UserRepository users;
    private BanRepository bans;
    private MuteRepository mutes;
    private EventService events;

    @Inject
    public UserService(PluginConfig config, EventService events, UserRepository users, BanRepository bans, MuteRepository mutes) {
        this.config = config;
        this.events = events;
        this.users = users;
        this.bans = bans;
        this.mutes = mutes;
    }

    public User get(String name) {
        return users.findByAlias(name);
    }

    public User get(UUID id) {
        return users.find(id);
    }

    public User create(UUID id, String name, Instant lastPlayed) {
        User user = new User(id, name);
        user.setLastActive(lastPlayed);
        users.save(user);

        return user;
    }

    public User create(Player player) {
        UUID id;
        if(config.isOffline()) {
            // Potential exploit patched in 1.5, details below:
            // - A player joins as ID 0, name Gandalf
            // - Player gets warnings or mutes on offline server
            // - Player joins with the same ID, but as name Merlin
            // - Gandalf gets renamed to Merlin because ID is the primary key in the table
            // - Player joins with new UUID, name Gandalf.
            // - Player has effectively transferred all mutes/warnings to Merlin

            // To fix this, we don't trust client UUID's if we're offline. We generate our own, here.
            id = UUID.randomUUID();
        } else {
            id = player.getUniqueId();
        }

        return create(id, player.getName(), Instant.ofEpochMilli(player.getLastPlayed()));
    }

    public User get(Player player) {
        if(config.isOffline()) {
            return get(player.getName());
        } else {
            return get(player.getUniqueId());
        }
    }

    public User getOrCreate(Player player) {
        User user = get(player);

        if(user != null) {
            if(!player.getName().equals(user.getName())) {
                user.setName(player.getName());
            }
            return user;
        }

        return create(player);
    }

    public Ban getBan(User user) {
        for(Ban ban : user.getBans()) {
            if(RestrictionUtil.isActive(ban)) return ban;
        }

        return null;
    }

    public Mute getMute(User user) {
        for(Mute mute : user.getMutes()) {
            if(RestrictionUtil.isActive(mute)) return mute;
        }

        return null;
    }

    public void onJoin(User user) throws RejectedException {
        Ban ban = getBan(user);
        if(ban != null) {
            throw new RejectedException("ban.denied")
                    .with("name", user.getName())
                    .with("reason", ban.getReason())
                    .with("source", ban.getSource() == null ? "Console" : ban.getSource().getName())
                    .with("duration", ban.getExpiresAt());
        }

        user.setLastActive(Instant.now());
    }

    public void onChat(User user) throws RejectedException {
        Mute mute = getMute(user);
        if(mute == null) return;

        throw new RejectedException("mute.denied")
                .with("name", user.getName())
                .with("reason", mute.getReason())
                .with("source", mute.getSource() == null ? "Console" : mute.getSource().getName())
                .with("duration", mute.getExpiresAt());
    }

    public boolean isChatCommand(String command) {
        if(config.getChatCommands().contains(command.toLowerCase())) {
            // It's the same as chatting
            return true;
        }

        return false;
    }

    public void ban(User source, User user, String reason, Duration duration) throws RejectedException, CancelledException {
        Ban ban = new Ban();
        ban.setCreated(Instant.now());
        ban.setReason(reason);
        ban.setSource(source);

        if(duration != null) {
            ban.setExpiresAt((Instant) duration.addTo(ban.getCreated()));
        }

        RestrictionUtil.assertRestrictionLonger(user.getBans(), ban);

        BanUserEvent event = new BanUserEvent(source, user, ban);
        events.call(event);

        if(event.isCancelled()) {
            throw new CancelledException();
        }

        // Must not have mutated the ban to be too short, so we check this again
        RestrictionUtil.assertRestrictionLonger(user.getBans(), ban);

        bans.save(ban);
        user.getBans().add(ban);
    }

    public void mute(User source, User user, String reason, Duration duration) throws RejectedException, CancelledException {
        Mute mute = new Mute();
        mute.setCreated(Instant.now());
        mute.setReason(reason);
        mute.setSource(source);

        if(duration != null) {
            mute.setExpiresAt(((Instant) duration.addTo(mute.getCreated())));
        }

        RestrictionUtil.assertRestrictionLonger(user.getMutes(), mute);
        MuteUserEvent event = new MuteUserEvent(source, user, mute);
        events.call(event);

        if (event.isCancelled()) {
            throw new CancelledException();
        }

        RestrictionUtil.assertRestrictionLonger(user.getMutes(), mute);

        mutes.save(mute);

        user.getMutes().add(mute);
    }

    public void unmute(User source, User user) throws RejectedException, CancelledException {
        List<Mute> list = user.getMutes();
        if(!RestrictionUtil.isActive(list)) {
            throw new RejectedException("mute.error.not-muted").with("name", user.getName());
        }

        List<Mute> revocable = new ArrayList<>(list.size());

        for (Mute mute : list) {
            if(!RestrictionUtil.isActive(mute)) continue;

            UnmuteUserEvent event = new UnmuteUserEvent(source, user, mute);
            events.call(event);

            if (event.isCancelled()) {
                throw new CancelledException();
            }

            revocable.add(mute);
        }

        for (Mute mute : revocable) {
            mute.setRevokedAt(Instant.now());
            mute.setRevoker(source);
        }
    }

    public void unban(User source, User user) throws RejectedException, CancelledException {
        List<Ban> list = user.getBans();
        if(!RestrictionUtil.isActive(list)) {
            throw new RejectedException("ban.error.not-banned").with("name", user.getName());
        }

        List<Ban> enforced = new ArrayList<>(list.size());

        for(Ban ban : list) {
            if (!RestrictionUtil.isActive(ban)) continue;

            UnbanUserEvent event = new UnbanUserEvent(source, user, ban);
            events.call(event);

            if (event.isCancelled()) {
                throw new CancelledException();
            }

            enforced.add(ban);
        }

        for (Ban ban : enforced) {
            if (!RestrictionUtil.isActive(ban)) continue;

            ban.setRevokedAt(Instant.now());
            ban.setRevoker(source);
        }
    }

    public MessageBuilder report(User user, Locale locale) throws RejectedException {
        if(user == null || user.getAddresses().isEmpty()) {
            throw new RejectedException("iplookup.never");
        }

        MessageBuilder builder = locale.get();
        builder.with("name", user.getName());
        builder.with("firstActive", user.getFirstActive());
        builder.with("lastActive", user.getLastActive());

        // Legacy support < 1.5
        UserAddress lastAddress = user.getLastAddress();
        if(lastAddress != null) {
            builder.with("ip", lastAddress.getAddress().getHost());
        }

        Ban ban = getBan(user);
        if(ban != null) {
            String reason = ban.getReason();
            if(reason == null || reason.isEmpty()) reason = "No reason";
            builder.with("ban", reason); // Legacy support < 1.5
            builder.with("ban.reason", reason);
            builder.with("ban.source", ban.getSource() == null ? "Console" : ban.getSource().getName());
            builder.with("ban.expires", ban.getExpiresAt());
            builder.with("ban.created", ban.getCreated());
        }

        Mute mute = getMute(user);
        if(mute != null) {
            String reason = mute.getReason();
            if(reason == null || reason.isEmpty()) reason = "No reason";
            builder.with("mute", reason); // Legacy support < 1.5
            builder.with("mute.reason", reason);
            builder.with("mute.source", mute.getSource() == null ? "Console" : mute.getSource().getName());
            builder.with("mute.expires", mute.getExpiresAt());
            builder.with("mute.created", mute.getCreated());
        }

        List<String> addresses = new ArrayList<>(user.getAddresses().size());
        for(UserAddress userAddress : user.getAddresses()) {
            addresses.add(userAddress.getAddress().getHost());
        }

        // Most recent addresses first
        Collections.reverse(addresses);

        builder.with("addresses", addresses);

        return builder;
    }
}

