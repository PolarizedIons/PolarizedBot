package net.polarizedions.polarizedbot.util;

import net.polarizedions.polarizedbot.Bot;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class BotInfo {
    public static String version = "Unknown";
    public static String buildtime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(new Date());
    public static String githubRepo = "[none]";

    static {
        load();
    }

    static void load() {
        Properties buildInfo = new Properties();
        try {
            buildInfo.load(Bot.class.getResourceAsStream("/botinfo.txt"));
        }
        catch (IOException ex) {
            Bot.logger.error("Error loading build information", ex);
        }

        Class<BotInfo> clazz = BotInfo.class;
        for (Field field : clazz.getFields()) {
            String value = buildInfo.getProperty(field.getName());
            if (value == null || value.startsWith("${") && value.endsWith("}")) {
                continue;
            }

            try {
                field.set(null, value);
            }
            catch (IllegalAccessException ex) {
                Bot.logger.error("Error setting build info value", ex);
            }
        }
    }

    private BotInfo() {}
}
