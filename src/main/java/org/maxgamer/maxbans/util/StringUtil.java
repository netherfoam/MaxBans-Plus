package org.maxgamer.maxbans.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author netherfoam
 */
public class StringUtil {
    public static String expand(String template, Map<String, Object> substitutions) {
        String result = "";
        Matcher matcher = Pattern.compile("\\{\\{[^\\}\\}]*\\}\\}").matcher(template);

        int last = 0;
        while(matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String group = matcher.group();
            result += template.substring(last, start);
            last = end;

            int defaultSeparator = group.lastIndexOf('|');
            String identifier;
            String defaultValue;
            if(defaultSeparator >= 0) {
                identifier = group.substring(2, defaultSeparator);
                defaultValue = group.substring(defaultSeparator + 1, group.length() - 2);
            } else {
                identifier = group.substring(2, group.length() - 2);
                defaultValue = "MISSING";
            }

            Object value = substitutions.get(identifier);
            if(value == null) value = defaultValue;

            result += value;
        }
        result += template.substring(last);

        return result;
    }
}
