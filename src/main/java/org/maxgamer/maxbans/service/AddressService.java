package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.*;
import org.maxgamer.maxbans.repository.AddressRepository;
import org.maxgamer.maxbans.repository.BanRepository;
import org.maxgamer.maxbans.repository.MuteRepository;
import org.maxgamer.maxbans.util.RestrictionUtil;
import org.maxgamer.maxbans.util.geoip.GeoCountry;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author netherfoam
 */
public class AddressService {
    private BanRepository bans;
    private MuteRepository mutes;
    private AddressRepository addressRepository;
    private GeoIPService geoIPService;

    public AddressService(BanRepository bans, MuteRepository mutes, AddressRepository addressRepository, GeoIPService geoIPService) {
        this.bans = bans;
        this.mutes = mutes;
        this.addressRepository = addressRepository;
        this.geoIPService = geoIPService;
    }

    public Address get(String ip) {
        return addressRepository.find(ip);
    }

    public Address getOrCreate(String ip) {
        Address address = get(ip);
        if(address != null) return address;

        return create(ip);
    }

    public Address create(String ip) {
        Address address = new Address(ip);
        addressRepository.save(address);

        return address;
    }

    public Ban getBan(Address address) {
        for(Ban ban : address.getBans()) {
            if(RestrictionUtil.isActive(ban)) return ban;
        }

        return null;
    }

    public Mute getMute(Address address) {
        for(Mute mute : address.getMutes()) {
            if(RestrictionUtil.isActive(mute)) return mute;
        }

        return null;
    }

    public void mute(User source, Address address, String reason, Duration duration) throws RejectedException {
        Mute mute = new Mute();
        mute.setCreated(Instant.now());
        mute.setReason(reason);
        mute.setSource(source);

        if(duration != null) {
            mute.setExpiresAt(((Instant) duration.addTo(mute.getCreated())));
        }

        RestrictionUtil.assertRestrictionLonger(address.getMutes(), mute);

        mutes.save(mute);

        address.getMutes().add(mute);
        addressRepository.save(address);
    }

    public void ban(User source, Address address, String reason, Duration duration) throws RejectedException {
        Ban ban = new Ban();
        ban.setCreated(Instant.now());
        ban.setReason(reason);
        ban.setSource(source);

        if(duration != null) {
            ban.setExpiresAt((Instant) duration.addTo(ban.getCreated()));
        }

        RestrictionUtil.assertRestrictionLonger(address.getBans(), ban);
        bans.save(ban);
        address.getBans().add(ban);
        addressRepository.save(address);
    }

    public void unmute(User source, Address address) throws RejectedException {
        List<Mute> list = address.getMutes();
        if(!RestrictionUtil.isActive(list)) {
            throw new RejectedException("mute.error.not-muted").with("address", address.getHost());
        }

        for(Mute mute : list) {
            if(!RestrictionUtil.isActive(mute)) continue;

            mute.setRevokedAt(Instant.now());
            mute.setRevoker(source);

            mutes.save(mute);
        }
    }

    public void unban(User source, Address address) throws RejectedException {
        List<Ban> list = address.getBans();
        if(!RestrictionUtil.isActive(list)) {
            throw new RejectedException("ban.error.not-banned").with("address", address.getHost());
        }

        for(Ban ban : list) {
            if(!RestrictionUtil.isActive(ban)) continue;

            ban.setRevokedAt(Instant.now());
            ban.setRevoker(source);

            bans.save(ban);
        }
    }

    public void onChat(Address address) throws RejectedException {
        Mute mute = getMute(address);
        if(mute == null) return;

        throw new RejectedException("mute.denied")
                .with("banner", mute.getSource() == null ? "Console" : mute.getSource().getName())
                .with("reason", mute.getReason())
                .with("duration", mute.getExpiresAt());
    }

    public void onJoin(User user, String ip) throws RejectedException {
        Address address = addressRepository.find(ip);
        UserAddress userAddress = null;

        if(address == null) {
            address = create(ip);
            addressRepository.save(address);
        } else {
            for (UserAddress history : user.getAddresses()) {
                if(history.getAddress().getHost().equals(address.getHost())) {
                    userAddress = history;
                }
            }
        }

        if(userAddress == null) {
            userAddress = new UserAddress(user, address);
        }

        Ban ban = getBan(address);
        if(ban != null) {
            throw new RejectedException("ipban.kick")
                    .with("banner", ban.getSource() == null ? "Console" : ban.getSource().getName())
                    .with("reason", ban.getReason())
                    .with("duration", ban.getExpiresAt());
        }

        userAddress.setLastActive(Instant.now());

        user.getAddresses().add(userAddress);
    }

    public Locale.MessageBuilder report(Address address, Locale locale) throws RejectedException {
        if(address == null) {
            throw new RejectedException("iplookup.never");
        }

        List<UserAddress> history = address.getUsers();
        Locale.MessageBuilder builder = locale.get();

        if(!history.isEmpty()) {
            UserAddress userAddress = history.get(history.size() - 1);

            builder.with("lastActive", userAddress.getLastActive());
            builder.with("firstActive", userAddress.getFirstActive());

            User user = userAddress.getUser();
            builder.with("name", user.getName());

            GeoCountry country = geoIPService.getCountry(address.getHost());
            if (country != null) {
                builder.with("country", country.getCountryName());
                builder.with("continent", country.getContinentName());
            }

            Ban ban = getBan(address);
            if(ban != null) {
                builder.with("ban",ban.getExpiresAt());
            }

            Mute mute = getMute(address);
            if(mute != null) {
                builder.with("mute", mute.getExpiresAt());
            }

            List<User> users = address.getUsers().stream().map(UserAddress::getUser).collect(Collectors.toList());
            StringBuilder stringBuilder = new StringBuilder();
            for(User related : users) {
                // Don't include the user whose active
                if(related == user) continue;

                // Prefix with a comma if this isn't the first element
                if(stringBuilder.length() > 0) stringBuilder.append(", ");

                stringBuilder.append(related.getName());
            }
            builder.with("users", stringBuilder.toString());
        }

        return builder;
    }

    public Locale.MessageBuilder report(User user, Locale locale) throws RejectedException {
        if(user == null || user.getAddresses().isEmpty()) {
            throw new RejectedException("iplookup.never");
        }

        List<UserAddress> history = user.getAddresses();
        Locale.MessageBuilder builder = locale.get();

        if(!history.isEmpty()) {
            UserAddress userAddress = history.get(history.size() - 1);

            builder.with("lastActive", userAddress.getLastActive());
            builder.with("firstActive", userAddress.getFirstActive());

            Address address = userAddress.getAddress();
            builder.with("ip", address.getHost());

            GeoCountry country = geoIPService.getCountry(address.getHost());
            if (country != null) {
                builder.with("country", country.getCountryName());
                builder.with("continent", country.getContinentName());
            }

            if(RestrictionUtil.isActive(address.getBans())) {
                builder.with("ban", address.getBans().get(0).getExpiresAt());
            }

            if(RestrictionUtil.isActive(address.getMutes())) {
                builder.with("mute", address.getMutes().get(0).getExpiresAt());
            }

            List<User> users = address.getUsers().stream().map(UserAddress::getUser).collect(Collectors.toList());
            StringBuilder stringBuilder = new StringBuilder();
            for(User related : users) {
                // Don't include the user whose active
                if(related == user) continue;

                // Prefix with a comma if this isn't the first element
                if(stringBuilder.length() > 0) stringBuilder.append(", ");

                stringBuilder.append(related.getName());
            }
            builder.with("users", stringBuilder.toString());
        }

        // These override the IP restrictions, since the user was explicitly banned
        if(RestrictionUtil.isActive(user.getBans())) {
            builder.with("ban", user.getBans().get(0).getExpiresAt());
        }

        if(RestrictionUtil.isActive(user.getMutes())) {
            builder.with("mute", user.getMutes().get(0).getExpiresAt());
        }

        return builder;
    }
}
