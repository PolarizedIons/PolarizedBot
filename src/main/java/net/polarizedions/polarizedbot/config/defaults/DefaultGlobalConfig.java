package net.polarizedions.polarizedbot.config.defaults;

import com.google.gson.InstanceCreator;
import net.polarizedions.polarizedbot.config.GlobalConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DefaultGlobalConfig implements InstanceCreator<GlobalConfig> {
    @Override
    public GlobalConfig createInstance(Type type) {
        GlobalConfig defaultConfig = new GlobalConfig();
        defaultConfig.globalAdmins = new ArrayList<>();
        defaultConfig.wolframAlphaApi = "";
        defaultConfig.botToken = "";
        defaultConfig.presenceDelay = 300;
        defaultConfig.presenceStrings = new String[] {
                "Playing with {owner-name}'s chemicals",
                "Spying on {guild-count} servers...",
                "Taking a cat nap. Zzz",
                "Spying on {random-member} O.O",
                "Taking over the world",
                "Rewriting my code",
                "Awakening Chell",
                "Awakening GlaDOS",
                "Downloading your search history",
        };
        return defaultConfig;
    }
}
