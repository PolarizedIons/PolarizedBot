package net.polarizedions.polarizedbot.util;

import net.polarizedions.polarizedbot.Bot;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class BuildInfo {
    public static String version = "Unknown";
    public static String buildtime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(new Date());
    public static String githubRepo = "";


    static {
        load();

        // TODO: BAD
        if (BuildInfo.githubRepo.startsWith("git@github.com:")) {
            BuildInfo.githubRepo = BuildInfo.githubRepo.replace("git@github.com:", "https://github.com/").replace(".git", "");
        }
        else if (BuildInfo.githubRepo.startsWith("${")) {
            BuildInfo.githubRepo = "?";
        }
    }

    static void load() {
        Properties buildInfo = new Properties();
        try {
            buildInfo.load(Bot.class.getResourceAsStream("/buildinfo.txt"));
        }
        catch (IOException ex) {
            Bot.logger.error("Error loading build information", ex);
        }

        Class<BuildInfo> clazz = BuildInfo.class;
        for (Field field : clazz.getFields()) {
            try {
                field.set(null, buildInfo.getProperty(field.getName()));
            }
            catch (IllegalAccessException ex) {
                Bot.logger.error("Error setting build info value", ex);
            }
        }
    }

    private BuildInfo() {}
}
