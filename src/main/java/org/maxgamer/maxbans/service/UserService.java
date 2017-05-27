package org.maxgamer.maxbans.service;

import org.bukkit.entity.Player;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.repository.BanRepository;
import org.maxgamer.maxbans.repository.MuteRepository;
import org.maxgamer.maxbans.repository.UserRepository;
import org.maxgamer.maxbans.util.RestrictionUtil;

import java.time.Duration;
import java.time.Instant;
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

    public UserService(PluginConfig config, UserRepository users, BanRepository bans, MuteRepository mutes) {
        this.config = config;
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
        return create(player.getUniqueId(), player.getName(), Instant.ofEpochMilli(player.getLastPlayed()));
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
        if(ban == null) return;

        throw new RejectedException("ban.denied")
                .with("reason", ban.getReason())
                .with("duration", ban.getExpiresAt());
    }

    public void onChat(User user) throws RejectedException {
        Mute mute = getMute(user);
        if(mute == null) return;

        throw new RejectedException("mute.denied")
                .with("name", user.getName())
                .with("reason", mute.getReason())
                .with("duration", mute.getExpiresAt());
    }

    public boolean isChatCommand(String command) {
        if(config.getChatCommands().contains(command.toLowerCase())) {
            // It's the same as chatting
            return true;
        }

        return false;
    }

    public void ban(User source, User user, String reason, Duration duration) throws RejectedException {
        Ban ban = new Ban();
        ban.setCreated(Instant.now());
        ban.setReason(reason);
        ban.setSource(source);
        
        if(duration != null) {
            ban.setExpiresAt((Instant) duration.addTo(ban.getCreated()));
        }

        RestrictionUtil.assertRestrictionLonger(user.getBans(), ban);
        bans.save(ban);
        user.getBans().add(ban);
        users.save(user);
    }
    
    public void mute(User source, User user, String reason, Duration duration) throws RejectedException {
        Mute mute = new Mute();
        mute.setCreated(Instant.now());
        mute.setReason(reason);
        mute.setSource(source);
        
        if(duration != null) {
            mute.setExpiresAt(((Instant) duration.addTo(mute.getCreated())));
        }
        
        RestrictionUtil.assertRestrictionLonger(user.getMutes(), mute);
        
        mutes.save(mute);

        user.getMutes().add(mute);
        users.save(user);
    }

    public void unmute(User source, User user) throws RejectedException {
        List<Mute> list = user.getMutes();
        if(!RestrictionUtil.isActive(list)) {
            throw new RejectedException("mute.error.not-muted").with("name", user.getName());
        }

        for(Mute mute : list) {
            if(!RestrictionUtil.isActive(mute)) continue;

            mute.setRevokedAt(Instant.now());
            mute.setRevoker(source);

            mutes.save(mute);
        }
    }

    public void unban(User source, User user) throws RejectedException {
        List<Ban> list = user.getBans();
        if(!RestrictionUtil.isActive(list)) {
            throw new RejectedException("ban.error.not-banned").with("name", user.getName());
        }

        for(Ban ban : list) {
            if(!RestrictionUtil.isActive(ban)) continue;

            ban.setRevokedAt(Instant.now());
            ban.setRevoker(source);

            bans.save(ban);
        }
    }
}

