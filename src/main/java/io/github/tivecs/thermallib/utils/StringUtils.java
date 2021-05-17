package io.github.tivecs.thermallib.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class StringUtils {

    public static String colored(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> colored(List<String> text){
        for (int i = 0; i < text.size(); i++) {
            text.set(i, colored(text.get(i)));
        }
        return text;
    }

}
