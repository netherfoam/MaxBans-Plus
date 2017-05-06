package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.UserAddress;
import org.maxgamer.maxbans.repository.AddressRepository;
import org.maxgamer.maxbans.util.RestrictionUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author netherfoam
 */
public class AddressService {
    private AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address get(String ip) {
        return addressRepository.find(ip);
    }

    public Locale.MessageBuilder report(User user, Locale locale) throws RejectedException {
        if(user == null || user.getAddresses().isEmpty()) {
            throw new RejectedException("iplookup.never");
        }

        List<UserAddress> history = user.getAddresses();
        UserAddress userAddress = history.get(history.size() - 1);

        Locale.MessageBuilder builder = locale.get();
        builder.with("lastActive", userAddress.getLastActive());
        builder.with("firstActive", userAddress.getFirstActive());

        Address address = userAddress.getAddress();
        builder.with("ip", address.getHost());

        if(RestrictionUtil.isActive(address.getBan())) {
            builder.with("ban", address.getBan().getExpiresAt());
        }

        if(RestrictionUtil.isActive(address.getMute())) {
            builder.with("mute", address.getMute().getExpiresAt());
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

        return builder;
    }
}
