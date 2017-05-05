package org.maxgamer.maxbans.command;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;

/**
 * @author netherfoam
 */
public class RestrictionCommandExecutorTest {
    /**
     * A subclass for testing
     */
    private static class DummyCommandExecutor extends RestrictionCommandExecutor {
        public DummyCommandExecutor(Server server, Locale locale, String permission) {
            super(server, locale, permission);
        }

        @Override
        public void restrict(CommandSender source, Player player, Duration duration, String reason) throws RejectedException {

        }
    }

    private static final String PERMISSION = "maxbans.test";

    @Test
    public void testWithReason() throws RejectedException {
        Server server = mock(Server.class);
        Player player = mock(Player.class);
        Locale locale = mock(Locale.class);
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);

        doReturn(player).when(server).getPlayer(any(String.class));
        doReturn(true).when(sender).hasPermission(eq(PERMISSION));

        DummyCommandExecutor executor = spy(new DummyCommandExecutor(server, locale, PERMISSION));

        String[] args = "player 5 hours for being rude".split(" ");
        executor.onCommand(sender, command, "dummy", args);

        verify(server, times(1)).getPlayer(any(String.class));
        verify(executor, times(1)).restrict(eq(sender), eq(player), eq(Duration.of(5, ChronoUnit.HOURS)), eq("for being rude"));
    }

    @Test
    public void testWithoutReason() throws RejectedException {
        Server server = mock(Server.class);
        Player player = mock(Player.class);
        Locale locale = mock(Locale.class);
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);

        doReturn(player).when(server).getPlayer(any(String.class));
        doReturn(true).when(sender).hasPermission(eq(PERMISSION));

        DummyCommandExecutor executor = spy(new DummyCommandExecutor(server, locale, PERMISSION));

        String[] args = "player 5 hours".split(" ");
        executor.onCommand(sender, command, "dummy", args);

        verify(server, times(1)).getPlayer(any(String.class));
        verify(executor, times(1)).restrict(eq(sender), eq(player), eq(Duration.of(5, ChronoUnit.HOURS)), any(String.class));
    }

    @Test
    public void testWithoutDurationOrReason() throws RejectedException {
        Server server = mock(Server.class);
        Player player = mock(Player.class);
        Locale locale = mock(Locale.class);
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);

        doReturn(player).when(server).getPlayer(any(String.class));
        doReturn(true).when(sender).hasPermission(eq(PERMISSION));

        DummyCommandExecutor executor = spy(new DummyCommandExecutor(server, locale, PERMISSION));

        String[] args = "player".split(" ");
        executor.onCommand(sender, command, "dummy", args);

        verify(server, times(1)).getPlayer(any(String.class));
        verify(executor, times(1)).restrict(eq(sender), eq(player), eq(null), any(String.class));
    }

    @Test
    public void testWithoutDuration() throws RejectedException {
        Server server = mock(Server.class);
        Player player = mock(Player.class);
        Locale locale = mock(Locale.class);
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);

        doReturn(player).when(server).getPlayer(any(String.class));
        doReturn(true).when(sender).hasPermission(eq(PERMISSION));

        DummyCommandExecutor executor = spy(new DummyCommandExecutor(server, locale, PERMISSION));

        String[] args = "player for being rude".split(" ");
        executor.onCommand(sender, command, "dummy", args);

        verify(server, times(1)).getPlayer(any(String.class));
        verify(executor, times(1)).restrict(eq(sender), eq(player), eq(null), eq("for being rude"));
    }

    @Test
    public void testWithoutPermission() throws RejectedException {
        Server server = mock(Server.class);
        Player player = mock(Player.class);
        Locale locale = mock(Locale.class);
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);

        doReturn(false).when(sender).hasPermission(eq(PERMISSION));

        DummyCommandExecutor executor = spy(new DummyCommandExecutor(server, locale, PERMISSION));

        String[] args = "player".split(" ");
        executor.onCommand(sender, command, "dummy", args);

        verify(server, never()).getPlayer(any(String.class));
        verify(executor, never()).restrict(eq(sender), eq(player), eq(null), eq("for being rude"));

        // We should have sent the sender a message saying they don't have permission
        verify(sender, times(1)).sendMessage(any(String.class));
    }

    @Test
    public void testFailingCommand() throws RejectedException {
        Server server = mock(Server.class);
        Player player = mock(Player.class);
        Locale locale = mock(Locale.class);
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);

        doReturn(player).when(server).getPlayer(any(String.class));
        doReturn(true).when(sender).hasPermission(eq(PERMISSION));

        DummyCommandExecutor executor = spy(new DummyCommandExecutor(server, locale, PERMISSION));
        doThrow(new RejectedException("not.ok")).when(executor).restrict(any(), any(), any(), any());

        String[] args = "player for being rude".split(" ");
        executor.onCommand(sender, command, "dummy", args);

        verify(server, times(1)).getPlayer(any(String.class));
        verify(executor, times(1)).restrict(eq(sender), eq(player), eq(null), eq("for being rude"));

        // We should have sent the sender a message saying they don't have permission
        verify(sender, times(1)).sendMessage(any(String.class));

        // But the target shouldn't ever be told anything
        verify(player, never()).sendMessage(any(String.class));
    }
}
