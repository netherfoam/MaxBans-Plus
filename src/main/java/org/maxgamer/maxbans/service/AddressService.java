package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.event.*;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.*;
import org.maxgamer.maxbans.repository.AddressRepository;
import org.maxgamer.maxbans.repository.BanRepository;
import org.maxgamer.maxbans.repository.MuteRepository;
import org.maxgamer.maxbans.util.RestrictionUtil;
import org.maxgamer.maxbans.util.geoip.GeoCountry;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
    private EventService eventService;

    @Inject
    public AddressService(BanRepository bans, MuteRepository mutes, AddressRepository addressRepository, GeoIPService geoIPService, EventService eventService) {
        this.bans = bans;
        this.mutes = mutes;
        this.addressRepository = addressRepository;
        this.geoIPService = geoIPService;
        this.eventService = eventService;
    }

    public Address get(String ip) {
        return addressRepository.find(ip);
    }

    public Address getOrCreate(String ip) {
        Address address = get(ip);
        if (address != null) return address;

        return create(ip);
    }

    public Address create(String ip) {
        Address address = new Address(ip);
        addressRepository.save(address);

        return address;
    }

    public Ban getBan(Address address) {
        for (Ban ban : address.getBans()) {
            if (RestrictionUtil.isActive(ban)) return ban;
        }

        return null;
    }

    public Mute getMute(Address address) {
        for (Mute mute : address.getMutes()) {
            if (RestrictionUtil.isActive(mute)) return mute;
        }

        return null;
    }

    public void mute(User source, Address address, String reason, Duration duration) throws RejectedException, CancelledException {
        Mute mute = new Mute();
        mute.setCreated(Instant.now());
        mute.setReason(reason);
        mute.setSource(source);

        if (duration != null) {
            mute.setExpiresAt(((Instant) duration.addTo(mute.getCreated())));
        }

        RestrictionUtil.assertRestrictionLonger(address.getMutes(), mute);

        MuteAddressEvent event = new MuteAddressEvent(source, address, mute);
        eventService.call(event);

        if (event.isCancelled()) {
            throw new CancelledException();
        }

        RestrictionUtil.assertRestrictionLonger(address.getMutes(), mute);

        mutes.save(mute);

        address.getMutes().add(mute);
    }

    public void ban(User source, Address address, String reason, Duration duration) throws RejectedException, CancelledException {
        Ban ban = new Ban();
        ban.setCreated(Instant.now());
        ban.setReason(reason);
        ban.setSource(source);

        if (duration != null) {
            ban.setExpiresAt((Instant) duration.addTo(ban.getCreated()));
        }

        RestrictionUtil.assertRestrictionLonger(address.getBans(), ban);
        BanAddressEvent event = new BanAddressEvent(source, address, ban);
        eventService.call(event);

        if (event.isCancelled()) {
            throw new CancelledException();
        }

        bans.save(ban);
        address.getBans().add(ban);
    }

    public void unmute(User source, Address address) throws RejectedException, CancelledException {
        List<Mute> list = address.getMutes();
        if (!RestrictionUtil.isActive(list)) {
            throw new RejectedException("mute.error.not-muted").with("address", address.getHost());
        }

        List<Mute> enforced = new ArrayList<>(list.size());

        for (Mute mute : list) {
            if (!RestrictionUtil.isActive(mute)) continue;

            UnmuteAddressEvent event = new UnmuteAddressEvent(source, address, mute);
            eventService.call(event);

            if (event.isCancelled()) {
                throw new CancelledException();
            }

            enforced.add(mute);
        }

        for (Mute mute : enforced) {
            mute.setRevokedAt(Instant.now());
            mute.setRevoker(source);
        }
    }

    public void unban(User source, Address address) throws RejectedException, CancelledException {
        List<Ban> list = address.getBans();
        if (!RestrictionUtil.isActive(list)) {
            throw new RejectedException("ban.error.not-banned").with("address", address.getHost());
        }

        List<Ban> revocable = new ArrayList<>(list.size());

        for (Ban ban : list) {
            if (!RestrictionUtil.isActive(ban)) continue;

            UnbanAddressEvent event = new UnbanAddressEvent(source, address, ban);
            eventService.call(event);

            if (event.isCancelled()) {
                throw new CancelledException();
            }

            revocable.add(ban);
        }

        for (Ban ban : revocable) {
            ban.setRevokedAt(Instant.now());
            ban.setRevoker(source);
        }
    }

    public void onChat(Address address) throws RejectedException {
        Mute mute = getMute(address);
        if (mute == null) return;

        throw new RejectedException("mute.denied")
                .with("address", address.getHost())
                .with("banner", mute.getSource())
                .with("reason", mute.getReason())
                .with("duration", mute.getExpiresAt());
    }

    public void onJoin(User user, String ip) throws RejectedException {
        Address address = addressRepository.find(ip);
        UserAddress userAddress = null;

        if (address == null) {
            address = create(ip);
        } else {
            for (UserAddress history : user.getAddresses()) {
                if (history.getAddress().getHost().equals(address.getHost())) {
                    userAddress = history;
                }
            }
        }

        if (userAddress == null) {
            userAddress = new UserAddress(user, address);
        }

        Ban ban = getBan(address);
        if (ban != null) {
            throw new RejectedException("ipban.denied")
                    .with("address", ip)
                    .with("name", user)
                    .with("source", ban.getSource())
                    .with("reason", ban.getReason())
                    .with("duration", ban.getExpiresAt());
        }

        userAddress.setLastActive(Instant.now());

        user.getAddresses().add(userAddress);
    }

    public MessageBuilder report(Address address, Locale locale) throws RejectedException {
        if (address == null) {
            throw new RejectedException("iplookup.never");
        }

        List<UserAddress> history = address.getUsers();
        MessageBuilder builder = locale.get();
        builder.with("ip", address.getHost());

        if (!history.isEmpty()) {
            Instant lastActive = null;
            Instant firstActive = null;

            for (UserAddress userAddress : history) {
                if (lastActive == null || userAddress.getLastActive().isAfter(lastActive)) {
                    lastActive = userAddress.getLastActive();
                }

                if (firstActive == null || userAddress.getFirstActive().isBefore(firstActive)) {
                    firstActive = userAddress.getFirstActive();
                }
            }

            builder.with("lastActive", lastActive);
            builder.with("firstActive", firstActive);
        }

        GeoCountry country = geoIPService.getCountry(address.getHost());
        if (country != null) {
            builder.with("country", country.getCountryName());
            builder.with("continent", country.getContinentName());
        }

        Ban ban = getBan(address);
        if (ban != null) {
            String reason = ban.getReason();
            if (reason == null || reason.isEmpty()) reason = "No reason";

            builder.with("ban", reason); // Legacy support < 1.5
            builder.with("ban.reason", reason);
            builder.withUserOrConsole("ban.source", ban.getSource());
            builder.with("ban.expires", ban.getExpiresAt());
            builder.with("ban.created", ban.getCreated());
        }

        Mute mute = getMute(address);
        if (mute != null) {
            String reason = mute.getReason();
            if (reason == null || reason.isEmpty()) reason = "No reason";
            builder.with("mute", reason); // Legacy support < 1.5
            builder.with("mute.reason", reason);
            builder.withUserOrConsole("mute.source", mute.getSource());
            builder.with("mute.expires", mute.getExpiresAt());
            builder.with("mute.created", mute.getCreated());
        }

        List<User> users = address.getUsers().stream().map(UserAddress::getUser).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for (User related : users) {
            // Prefix with a comma if this isn't the first element
            if (stringBuilder.length() > 0) stringBuilder.append(", ");

            stringBuilder.append(related.getName());
        }
        builder.with("users", stringBuilder.toString());

        return builder;
    }
}
