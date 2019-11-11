package org.maxgamer.maxbans.locale;

import junit.framework.Assert;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * TODO: Document this
 */
public class TooltipMessageTest {
    private static final String TEMPLATE_ID = "template.id";
    private Locale locale;
    private Map<String, String> messages;

    private CommandSender recipient;
    private CommandSender.Spigot recipientSpigot;

    @Before
    public void setup() {
        locale = mock(Locale.class);
        messages = new HashMap<>();

        doReturn(messages).when(locale).getMessages();

        recipient = mock(CommandSender.class);
        recipientSpigot = mock(CommandSender.Spigot.class);

        doReturn(recipientSpigot).when(recipient).spigot();
    }

    @Test
    public void testBasic() {
        messages.put(TEMPLATE_ID, "some message");
        TooltipMessage message = new TooltipMessage(locale, new HashMap<>(), TEMPLATE_ID);

        ArgumentCaptor<BaseComponent> captor = ArgumentCaptor.forClass(BaseComponent.class);

        message.send(recipient);

        verify(recipient, times(1)).spigot();
        verify(locale, times(1)).getMessages();
        verify(recipientSpigot, times(1)).sendMessage(new BaseComponent[]{captor.capture()});

        List<BaseComponent> components = captor.getAllValues();

        Assert.assertEquals("Expect a Single Component", 1, components.size());

        BaseComponent component = components.get(0);
        Assert.assertEquals("Expect white", ChatColor.WHITE, component.getColor());
        Assert.assertEquals("Expect some message", "some message", component.toPlainText());
    }

    @Test
    public void testTemplate() {
        messages.put(TEMPLATE_ID, "some &amessage");

        TooltipMessage message = new TooltipMessage(locale, new HashMap<>(), TEMPLATE_ID);

        ArgumentCaptor<BaseComponent> captor = ArgumentCaptor.forClass(BaseComponent.class);

        message.send(recipient);
        verify(recipientSpigot, times(1)).sendMessage(new BaseComponent[]{captor.capture()});

        List<BaseComponent> components = captor.getAllValues();

        Assert.assertEquals("Expect 2 components", 2, components.size());

        BaseComponent first = components.get(0);
        Assert.assertEquals("Expect white", ChatColor.WHITE, first.getColor());
        Assert.assertEquals("Expect 'some '", "some ", first.toPlainText());

        BaseComponent second = components.get(1);
        Assert.assertEquals("Expect green", ChatColor.GREEN, second.getColor());
        Assert.assertEquals("Expect 'message'", "message", second.toPlainText());
    }

    @Test
    public void testValueDoesntAffectTrailingColor() {
        messages.put(TEMPLATE_ID, "some &b{{thing}} else");

        BaseComponent[] thing = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&aTEXT"));
        TooltipMessage message = new TooltipMessage(locale, Collections.singletonMap("thing", thing), TEMPLATE_ID);

        ArgumentCaptor<BaseComponent> captor = ArgumentCaptor.forClass(BaseComponent.class);

        message.send(recipient);
        verify(recipientSpigot, times(1)).sendMessage(new BaseComponent[]{captor.capture()});

        List<BaseComponent> components = captor.getAllValues();

        Assert.assertEquals("Expect 3 components", 3, components.size());

        BaseComponent first = components.get(0);
        Assert.assertEquals("Expect white", ChatColor.WHITE, first.getColor());
        Assert.assertEquals("Expect 'some '", "some ", first.toPlainText());

        BaseComponent second = components.get(1);
        Assert.assertEquals("Expect green", ChatColor.GREEN, second.getColor());
        Assert.assertEquals("Expect '&aTEXT'", "TEXT", second.toPlainText());

        BaseComponent third = components.get(2);
        Assert.assertEquals("Expect aqua", ChatColor.AQUA, third.getColor());
        Assert.assertEquals("Expect ' else'", " else", third.toPlainText());
    }

    @Test
    public void testLiteralAffectsColorlessValue() {
        messages.put(TEMPLATE_ID, "Hello &b{{thing}}");

        TextComponent thing = new TextComponent();
        thing.setText("World!");

        TooltipMessage message = new TooltipMessage(locale, Collections.singletonMap("thing", thing), TEMPLATE_ID);

        ArgumentCaptor<BaseComponent> captor = ArgumentCaptor.forClass(BaseComponent.class);

        message.send(recipient);
        verify(recipientSpigot, times(1)).sendMessage(new BaseComponent[]{captor.capture()});

        List<BaseComponent> components = captor.getAllValues();

        Assert.assertEquals("Expect 2 components", 2, components.size());

        BaseComponent first = components.get(0);
        Assert.assertEquals("Expect white", ChatColor.WHITE, first.getColor());
        Assert.assertEquals("Expect 'Hello '", "Hello ", first.toPlainText());

        BaseComponent second = components.get(1);
        Assert.assertEquals("Expect aqua", ChatColor.AQUA, second.getColor());
        Assert.assertEquals("Expect 'World!'", "World!", second.toPlainText());
    }
}
