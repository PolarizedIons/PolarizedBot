package net.polarizedions.polarizedbot.config.defaults;

import com.google.gson.InstanceCreator;
import net.polarizedions.polarizedbot.config.GlobalConfig;

import java.lang.reflect.Type;

public class DefaultGlobalConfig implements InstanceCreator<GlobalConfig> {
    @Override
    public GlobalConfig createInstance(Type type) {
        GlobalConfig defaultConfig = new GlobalConfig();
        defaultConfig.owner = "";
        defaultConfig.wolframAlphaApi = "";
        defaultConfig.botToken = "";
        defaultConfig.presenceDelay = 300;
        defaultConfig.presenceStrings = new String[] { "Serving {guilds-num} guilds dutifully", "Playing with {owner-name}'s chemicals", "Napping..."};
        return defaultConfig;
    }
}
