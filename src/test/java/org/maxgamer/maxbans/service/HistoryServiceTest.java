package org.maxgamer.maxbans.service;

import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Message;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.transaction.TransactionLayer;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Ensure our history service behaves as expected
 */
public class HistoryServiceTest extends PluginContextTest {
    private HistoryService historyService;
    private UserService userService;

    @Before
    public void setup() {
        historyService = getContext().components().services().history();
        userService = getContext().components().services().user();
    }

    @Test
    public void getBySenderWithContents() throws RejectedException, CancelledException {
        User user = userService.create(UUID.randomUUID(), "Joe", Instant.EPOCH);
        User banner = userService.create(UUID.randomUUID(), "MrAdmin", Instant.EPOCH);

        userService.ban(banner, user, "Reason", Duration.ofHours(2));

        try (TransactionLayer tx = getContext().components().transactor().transact()) {
            List<Message> messages = historyService.getHistory(1, banner);
            System.out.println(String.join("\n", messages.stream().map(Message::toString).collect(Collectors.toList())));
        }
    }
}
