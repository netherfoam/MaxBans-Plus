package org.maxgamer.maxbans.service;

import junit.framework.Assert;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.UserAddress;
import org.maxgamer.maxbans.repository.AddressRepository;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.transaction.TransactionLayer;

import java.time.Instant;
import java.util.UUID;

/**
 * @author netherfoam
 */
public class AddressServiceTest extends PluginContextTest implements IntegrationTest {
    @Test
    public void testBan() throws RejectedException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        Assert.assertNull("Expect address to be unbanned", addresses.getBan(address));
        addresses.ban(null, address, "Breaking Rules", null);
        Assert.assertNotNull("Expect address to be banned", addresses.getBan(address));
    }

    @Test
    public void testMute() throws RejectedException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        Assert.assertNull("Expect address to be unmuted", addresses.getMute(address));
        addresses.mute(null, address, "Breaking Rules", null);
        Assert.assertNotNull("Expect address to be muted", addresses.getMute(address));
    }

    @Test
    public void testUnmute() throws RejectedException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        addresses.mute(null, address, "Breaking Rules", null);
        addresses.unmute(null, address);
        Assert.assertNull("Expect address to be unmuted", addresses.getMute(address));
    }

    @Test
    public void testUnban() throws RejectedException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        addresses.ban(null, address, "Breaking Rules", null);
        addresses.unban(null, address);
        Assert.assertNull("Expect address to be unbanned", addresses.getBan(address));
    }

    @Test
    public void testReport() throws MessageException {
        UserService users = getContext().components().services().user();
        AddressService service = getContext().components().services().address();

        UUID id = UUID.randomUUID();
        try (TransactionLayer tx = getContext().components().transactor().transact()) {
            User user = users.create(id, "Address_McGee", Instant.now());

            AddressRepository repository = getContext().components().repositories().address();
            Address address = new Address("127.0.0.1");
            repository.save(address);

            UserAddress primary = new UserAddress(user, address);
            user.getAddresses().add(primary);
            address.getUsers().add(primary);

            User friend = users.create(UUID.randomUUID(), "Address_McGee_Other", Instant.now());
            UserAddress secondary = new UserAddress(friend, address);
            user.getAddresses().add(secondary);
            address.getUsers().add(secondary);
        }

        try (TransactionLayer tx = getContext().components().transactor().transact()) {
            Address address = service.get("127.0.0.1");
            Assert.assertFalse(address.getUsers().isEmpty());
        }

        try (TransactionLayer tx = getContext().components().transactor().transact()) {
            MessageBuilder builder = users.report(users.get(id), new Locale());

            Assert.assertEquals("expect no ban", builder.preview("ban"), null);
            Assert.assertEquals("expect no mute", builder.preview("mute"), null);
            Assert.assertEquals("expect IP address", builder.preview("ip"), "127.0.0.1");
            Assert.assertNotNull("firstActive", builder.preview("firstActive"));
            Assert.assertNotNull("lastActive", builder.preview("lastActive"));
            Assert.assertTrue("expect to find related user", builder.preview("addresses").toString().contains("127.0.0.1"));
        }
    }
}
