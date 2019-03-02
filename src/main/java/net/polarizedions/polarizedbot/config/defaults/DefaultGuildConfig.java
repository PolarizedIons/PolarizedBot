package net.polarizedions.polarizedbot.config.defaults;

import com.google.gson.InstanceCreator;
import net.polarizedions.polarizedbot.config.GuildConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultGuildConfig implements InstanceCreator<GuildConfig> {
    @Override
    public GuildConfig createInstance(Type type) {
        GuildConfig defaultConfig = new GuildConfig();
        defaultConfig.lang = "en";
        defaultConfig.commandPrefix = "!";
        defaultConfig.rank = new HashMap<>();
        defaultConfig.ignoredUsers = new ArrayList<>();
        defaultConfig.disabledCommands = new ArrayList<>();
        defaultConfig.disabledResponders = new ArrayList<>();
        return defaultConfig;
    }
}
