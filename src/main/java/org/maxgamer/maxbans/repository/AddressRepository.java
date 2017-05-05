package org.maxgamer.maxbans.repository;

import org.hibernate.SessionFactory;
import org.maxgamer.maxbans.orm.Address;

/**
 * @author netherfoam
 */
public class AddressRepository extends Repository<String, Address> {
    public AddressRepository(SessionFactory factory) {
        super(factory, String.class, Address.class);
    }
}
