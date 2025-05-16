package ru.he3hauka.htitles.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexSupport {

    private static final Pattern HEX_PATTERN_AMP = Pattern.compile("&#([a-fA-F\\d]{6})");
    private static final Pattern HEX_PATTERN_PLAIN = Pattern.compile("(?<!&)#([a-fA-F\\d]{6})");

    public static final char COLOR_CHAR = '§';

    public static String format(String message) {
        message = applyHex(message, HEX_PATTERN_AMP);
        message = applyHex(message, HEX_PATTERN_PLAIN);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String applyHex(String message, Pattern pattern) {
        Matcher matcher = pattern.matcher(message);
        StringBuilder builder = new StringBuilder(message.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(builder,
                    COLOR_CHAR + "x" +
                            COLOR_CHAR + group.charAt(0) +
                            COLOR_CHAR + group.charAt(1) +
                            COLOR_CHAR + group.charAt(2) +
                            COLOR_CHAR + group.charAt(3) +
                            COLOR_CHAR + group.charAt(4) +
                            COLOR_CHAR + group.charAt(5));
        }
        return matcher.appendTail(builder).toString();
    }
}
