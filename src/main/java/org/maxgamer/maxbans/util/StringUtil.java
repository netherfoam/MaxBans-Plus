package org.maxgamer.maxbans.util;

import java.util.LinkedList;
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

            group = group.substring(2, group.length() - 2);
            String[] options = split(group, '|');
            String value = expand(options, substitutions);

            result += value;
        }
        result += template.substring(last);

        return result;
    }

    public static String expand(String[] options, Map<String, Object> substitutions) {
        for(String option : options) {
            Object value = substitutions.get(option);
            if(value == null) continue;
            String text = value.toString();
            if(text.isEmpty()) continue;

            return text;
        }

        return options[options.length - 1];
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
}
