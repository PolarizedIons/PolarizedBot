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
        return defaultConfig;
    }
}
