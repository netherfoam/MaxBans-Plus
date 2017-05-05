package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.repository.AddressRepository;

/**
 * @author netherfoam
 */
public class AddressService {
    private AddressRepository addressRepository;

    public Address get(String ip) {
        return addressRepository.find(ip);
    }
}
