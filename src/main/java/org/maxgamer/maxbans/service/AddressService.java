package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.UserAddress;
import org.maxgamer.maxbans.repository.AddressRepository;
import org.maxgamer.maxbans.util.RestrictionUtil;
import org.maxgamer.maxbans.util.geoip.GeoCountry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author netherfoam
 */
public class AddressService {
    private AddressRepository addressRepository;
    private GeoIPService geoIPService;

    public AddressService(AddressRepository addressRepository, GeoIPService geoIPService) {
        this.addressRepository = addressRepository;
        this.geoIPService = geoIPService;
    }

    public Address get(String ip) {
        return addressRepository.find(ip);
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
