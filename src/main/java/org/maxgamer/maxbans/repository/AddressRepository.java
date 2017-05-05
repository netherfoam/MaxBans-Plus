package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.transaction.Transactor;

/**
 * @author netherfoam
 */
public class AddressRepository extends Repository<String, Address> {
    public AddressRepository(Transactor worker) {
        super(worker, String.class, Address.class);
    }
}
