package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Address;

import javax.inject.Inject;

/**
 * @author netherfoam
 */
public class AddressRepository extends Repository<String, Address> {
    @Inject
    public AddressRepository() {
        super(String.class, Address.class);
    }
}
