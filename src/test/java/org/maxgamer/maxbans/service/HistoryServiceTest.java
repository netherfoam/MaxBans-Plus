package org.maxgamer.maxbans.service;

import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Ensure our history service behaves as expected
 */
public class HistoryServiceTest extends PluginContextTest {
    private HistoryService historyService;
    private UserService userService;
    private AddressService addressService;
    private Transactor transactorService;

    @Before
    public void setup() {
        historyService = getContext().components().services().history();
        userService = getContext().components().services().user();
        addressService = getContext().components().services().address();
        transactorService = getContext().components().transactor();
    }

    @Test
    public void getBySenderWithContents() throws RejectedException {
        User user = userService.create(UUID.randomUUID(), "Joe", Instant.EPOCH);
        User banner = userService.create(UUID.randomUUID(), "MrAdmin", Instant.EPOCH);

        userService.ban(banner, user, "Reason", Duration.ofHours(2));

        transactorService.work(session -> {
            List<String> messages = historyService.getHistory(1, banner);
            System.out.println(String.join("\n", messages));
        });
    }
}
