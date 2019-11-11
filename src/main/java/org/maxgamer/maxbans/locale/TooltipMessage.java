package org.maxgamer.maxbans.locale;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.util.StringUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * TODO: Document this
 */
public class TooltipMessage extends BukkitMessage {
    private static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    /**
     * Correct implementation of {@link TextComponent#fromLegacyText(String, ChatColor)}
     */
    public static BaseComponent[] fromLegacyText(String message, ChatColor defaultColor) {
        ArrayList<BaseComponent> components = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();
        Matcher matcher = URL_PATTERN.matcher(message);

        for(int i = 0; i < message.length(); ++i) {
            char c = message.charAt(i);
            TextComponent old;
            if (c == 167) {
                ++i;
                if (i >= message.length()) {
                    break;
                }

                c = message.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    c = (char)(c + 32);
                }

                ChatColor format = ChatColor.getByChar(c);
                if (format != null) {
                    if (builder.length() > 0) {
                        old = component;
                        component = new TextComponent(component);
                        old.setText(builder.toString());
                        builder = new StringBuilder();
                        components.add(old);
                    }

                    switch(format) {
                        case BOLD:
                            component.setBold(true);
                            break;
                        case ITALIC:
                            component.setItalic(true);
                            break;
                        case UNDERLINE:
                            component.setUnderlined(true);
                            break;
                        case STRIKETHROUGH:
                            component.setStrikethrough(true);
                            break;
                        case MAGIC:
                            component.setObfuscated(true);
                            break;
                        case RESET:
                            format = defaultColor;
                        default:
                            component = new TextComponent();
                            component.setColor(format);
                    }
                }
            } else {
                int pos = message.indexOf(' ', i);
                if (pos == -1) {
                    pos = message.length();
                }

                if (matcher.region(i, pos).find()) {
                    if (builder.length() > 0) {
                        old = component;
                        component = new TextComponent(component);
                        old.setText(builder.toString());
                        builder = new StringBuilder();
                        components.add(old);
                    }

                    old = component;
                    component = new TextComponent(component);
                    String urlString = message.substring(i, pos);
                    component.setText(urlString);
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlString.startsWith("http") ? urlString : "http://" + urlString));
                    components.add(component);
                    i += pos - i - 1;
                    component = old;
                } else {
                    builder.append(c);
                }
            }
        }

        if (builder.length() > 0 || component.getColorRaw() != defaultColor) {
            component.setText(builder.toString());
            components.add(component);
        }

        if (components.isEmpty()) {
            components.add(new TextComponent(""));
        }

        return components.toArray(new BaseComponent[components.size()]);
    }

    public static BaseComponent[] expand(String template, Map<String, BaseComponent[]> substitutions) {
        List<BaseComponent> components = new ArrayList<>();

        Matcher matcher = Pattern.compile("\\{\\{[^\\}\\}]*\\}\\}").matcher(template);

        // This is a dummy value for copying formatting from the previous literal
        BaseComponent previousLiteral = new TextComponent();

        int last = 0;
        while(matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String group = matcher.group();
            BaseComponent[] literalText = fromLegacyText(template.substring(last, start), ChatColor.WHITE);

            if (literalText.length > 0) {
                copyPrevious(previousLiteral, literalText);
                previousLiteral = literalText[literalText.length - 1];
            }

            components.addAll(Arrays.asList(literalText));
            last = end;

            group = group.substring(2, group.length() - 2);
            String[] options = split(group, '|');
            BaseComponent[] value = expand(options, substitutions);

            BaseComponent lastComponent = previousLiteral;
            for (BaseComponent v : value) {
                v.copyFormatting(lastComponent, ComponentBuilder.FormatRetention.FORMATTING, false);
                lastComponent = v;
            }

            components.addAll(Arrays.asList(value));
        }

        String lastLiteral = template.substring(last);
        if (!lastLiteral.isEmpty()) {
            BaseComponent[] literalText = fromLegacyText(lastLiteral, ChatColor.WHITE);
            copyPrevious(previousLiteral, literalText);

            components.addAll(Arrays.asList(literalText));
        }

        return components.stream()
                .filter(c -> !c.toPlainText().isEmpty())
                .toArray(BaseComponent[]::new);
    }

    private static void copyPrevious(BaseComponent previous, BaseComponent[] literalText) {
        for (int i = 0; i < literalText.length; i++) {
            if (i == 0) {
                // First iteration, copy from the last component
                if (previous == null) continue;

                literalText[i].copyFormatting(previous, ComponentBuilder.FormatRetention.FORMATTING, false);
                continue;
            }

            literalText[i].copyFormatting(literalText[i - 1], ComponentBuilder.FormatRetention.FORMATTING, false);
        }
    }

    public static BaseComponent[] expand(String[] options, Map<String, BaseComponent[]> substitutions) {
        for(String option : options) {
            BaseComponent[] value = substitutions.get(option);
            if(value == null) continue;
            if (value.length <= 0) continue;
            if (Arrays.stream(value).allMatch(c -> c.toLegacyText().isEmpty())) continue;

            return value;
        }

        // We just use the name instead
        return fromLegacyText(options[options.length - 1], ChatColor.WHITE);
    }

    private static String[] split(String text, char delimiter) {
        // Can't use string.split("\\|") because that splits "hello||" into just "hello" and not {"hello", "", ""} as it should

        LinkedList<String> components = new LinkedList<>();

        int start = 0;
        int end;

        while((end = text.indexOf(delimiter, start)) >= 0) {
            String s = text.substring(start, end);
            components.add(s);

            // +1 so we skip the | next.
            start = end + 1;
        }

        components.add(text.substring(start, text.length()));

        return components.toArray(new String[components.size()]);
    }

    public TooltipMessage(Locale locale, Map<String, Object> substitutions, String templateId) {
        super(locale, substitutions, templateId);
    }

    protected BaseComponent[] transformValueToComponents(CommandSender recipient, Object value) {
        if (value instanceof BaseComponent) {
            return new BaseComponent[]{(BaseComponent) value};
        } else if (value instanceof BaseComponent[]) {
            return (BaseComponent[]) value;
        }

        Object fallback = super.transform(value);
        if (fallback == null) return null;

        String legacyText = String.valueOf(fallback);

        return fromLegacyText(legacyText, ChatColor.WHITE);
    }

    @Override
    public void send(CommandSender recipient) {
        String template = locale.getMessages().get(this.templateId);
        if (template == null) throw new IllegalArgumentException("No such template: " + this.templateId);

        Map<String, BaseComponent[]> variableComponents = new HashMap<>(substitutions.size());
        for (Map.Entry<String, Object> entry : substitutions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            BaseComponent[] components = transformValueToComponents(recipient, value);

            variableComponents.put(key, components);
        }

        // Now we expand the template before parsing in variable substitutions
        template = ChatColor.translateAlternateColorCodes('&', template);

        BaseComponent[] components = expand(template, variableComponents);

        recipient.spigot().sendMessage(components);
    }

    @Override
    public String toString() {
        String template = locale.getMessages().get(this.templateId);
        if (template == null) throw new IllegalArgumentException("No such template: " + this.templateId);

        Map<String, Object> preprocessed = new HashMap<>(substitutions.size());
        for (Map.Entry<String, Object> entry : substitutions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof BaseComponent) {
                value = ((BaseComponent) value).toLegacyText();
            } else if (value instanceof BaseComponent[]) {
                value = Arrays.stream(((BaseComponent[]) value))
                        .map(component -> component.toLegacyText())
                        .collect(Collectors.joining());
            } else {
                value = super.transform(value);
            }

            preprocessed.put(key, value);
        }

        // Now we expand the template before parsing in variable substitutions
        template = ChatColor.translateAlternateColorCodes('&', template);

        String output = StringUtil.expand(template, preprocessed);
        if (next != null) {
            output += next.toString();
        }

        return output;
    }
}
