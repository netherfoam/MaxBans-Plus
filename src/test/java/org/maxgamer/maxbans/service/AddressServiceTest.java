package org.maxgamer.maxbans.service;

import org.bukkit.plugin.PluginManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.event.*;
import org.maxgamer.maxbans.exception.CancelledException;
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

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author netherfoam
 */
public class AddressServiceTest extends PluginContextTest implements IntegrationTest {
    private PluginManager pluginManager;

    @Before
    public void setup() {
        pluginManager = getContext().getPluginModule().getPluginManager();
    }

    @Test
    public void doNothing1() {

    }
    @Test
    public void doNothing2() {

    }

    @Test
    public void testBan() throws RejectedException, CancelledException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        Assert.assertNull("Expect address to be unbanned", addresses.getBan(address));
        addresses.ban(null, address, "Breaking Rules", null);
        Assert.assertNotNull("Expect address to be banned", addresses.getBan(address));

        verify(pluginManager, times(1)).callEvent(isA(BanAddressEvent.class));
    }

    @Test
    public void testMute() throws RejectedException, CancelledException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        Assert.assertNull("Expect address to be unmuted", addresses.getMute(address));
        addresses.mute(null, address, "Breaking Rules", null);
        Assert.assertNotNull("Expect address to be muted", addresses.getMute(address));

        verify(pluginManager, times(1)).callEvent(isA(MuteAddressEvent.class));
    }

    @Test
    public void testUnmute() throws RejectedException, CancelledException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        addresses.mute(null, address, "Breaking Rules", null);
        addresses.unmute(null, address);
        Assert.assertNull("Expect address to be unmuted", addresses.getMute(address));

        verify(pluginManager, times(1)).callEvent(isA(UnmuteAddressEvent.class));
    }

    @Test
    public void testUnban() throws RejectedException, CancelledException {
        AddressService addresses = getContext().components().services().address();
        Address address = addresses.create("127.0.0.1");

        addresses.ban(null, address, "Breaking Rules", null);
        addresses.unban(null, address);
        Assert.assertNull("Expect address to be unbanned", addresses.getBan(address));

        verify(pluginManager, times(1)).callEvent(isA(UnbanAddressEvent.class));
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
            MessageBuilder builder = users.report(users.get(id), getContext().components().locale());

            Assert.assertNull("expect no ban", builder.preview("ban"));
            Assert.assertNull("expect no mute", builder.preview("mute"));
            Assert.assertEquals("expect IP address", builder.preview("ip"), "127.0.0.1");
            Assert.assertNotNull("firstActive", builder.preview("firstActive"));
            Assert.assertNotNull("lastActive", builder.preview("lastActive"));
            Assert.assertTrue("expect to find related user", builder.preview("addresses").toString().contains("127.0.0.1"));
        }
    }
}
