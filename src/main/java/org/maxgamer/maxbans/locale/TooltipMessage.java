package org.maxgamer.maxbans.locale;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.util.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * TODO: Document this
 */
public class TooltipMessage extends Message {
    public static BaseComponent[] expand(String template, Map<String, BaseComponent[]> substitutions) {
        ComponentBuilder builder = new ComponentBuilder("");

        Matcher matcher = Pattern.compile("\\{\\{[^\\}\\}]*\\}\\}").matcher(template);

        int last = 0;
        while(matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String group = matcher.group();
            // TODO: @md5 is a tard and forgets to set the colour when the text is
            // TODO: an empty string. need to correct this shitty behaviour to
            // TODO: get colour codes working again when templates are like &a{{name}}
            // TODO: where it gets tokenized into "&a" and "{{name}}". Since &a is
            // TODO: converted to a component of empty text, the color gets forced
            // TODO: to green. Fucking retard.
            BaseComponent[] literalText = TextComponent.fromLegacyText(template.substring(last, start));
            builder.append(literalText);
            last = end;

            group = group.substring(2, group.length() - 2);
            String[] options = split(group, '|');
            BaseComponent[] value = expand(options, substitutions);

            builder.append(value);
        }

        String lastLiteral = template.substring(last);
        if (!lastLiteral.isEmpty()) {
            builder.append(TextComponent.fromLegacyText(lastLiteral));
        }

        BaseComponent[] components = builder.create();
        /*ChatColor lastColor = ChatColor.WHITE;

        for (BaseComponent component : components) {
            if (component.getColorRaw() == ChatColor.WHITE) {
                component.setColor(lastColor);
                continue;
            }

            lastColor = component.getColorRaw();
        }*/

        return components;
    }

    public static BaseComponent[] expand(String[] options, Map<String, BaseComponent[]> substitutions) {
        for(String option : options) {
            BaseComponent[] value = substitutions.get(option);
            if(value == null) continue;
            if (value.length <= 0) continue;
            if (Arrays.stream(value).noneMatch(c -> c.toLegacyText().isEmpty())) continue;

            return value;
        }

        // We just use the name instead
        return TextComponent.fromLegacyText(options[options.length - 1]);
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
        /*if (recipient == null) {
            if (value instanceof BaseComponent) {
                return new BaseComponent[]{(BaseComponent) value};
            }

            if (value instanceof BaseComponent[]) {
                return (BaseComponent[]) value;
            }
        }*/

        String legacyText = String.valueOf(super.transform(value));

        return TextComponent.fromLegacyText(legacyText);
    }

    @Override
    public void send(CommandSender recipient) {
        String template = locale.messages.get(this.templateId);
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
        String template = locale.messages.get(this.templateId);
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
