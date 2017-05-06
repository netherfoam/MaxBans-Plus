package org.maxgamer.maxbans.service;

import junit.framework.Assert;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.UserAddress;
import org.maxgamer.maxbans.repository.AddressRepository;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.time.Instant;
import java.util.UUID;

/**
 * @author netherfoam
 */
public class AddressServiceTest extends PluginContextTest implements IntegrationTest {
    @Test
    public void testReport() throws MessageException {
        UserService users = getContext().getUserService();
        AddressService service = getContext().getAddressService();

        UUID id = UUID.randomUUID();
        getContext().getTransactor().work(session -> {
            User user = users.create(id, "Address_McGee", Instant.now());

            AddressRepository repository = getContext().getAddressRepository();
            Address address = new Address("127.0.0.1");
            repository.save(address);

            UserAddress primary = new UserAddress(user, address);
            user.getAddresses().add(primary);
            address.getUsers().add(primary);

            User friend = users.create(UUID.randomUUID(), "Address_McGee_Other", Instant.now());
            UserAddress secondary = new UserAddress(friend, address);
            user.getAddresses().add(secondary);
            address.getUsers().add(secondary);
        });

        getContext().getTransactor().work(session -> {
            Address address = service.get("127.0.0.1");
            Assert.assertFalse(address.getUsers().isEmpty());
        });

        getContext().getTransactor().work(session -> {
            Locale.MessageBuilder builder = service.report(users.get(id), new Locale());

            Assert.assertEquals("expect no ban", builder.preview("ban"), null);
            Assert.assertEquals("expect no mute", builder.preview("mute"), null);
            Assert.assertEquals("expect IP address", builder.preview("ip"), "127.0.0.1");
            Assert.assertNotNull("firstActive", builder.preview("firstActive"));
            Assert.assertNotNull("lastActive", builder.preview("lastActive"));
            Assert.assertTrue("expect to find related user", builder.preview("users").toString().contains("Address_McGee_Other"));
        });
    }
}
