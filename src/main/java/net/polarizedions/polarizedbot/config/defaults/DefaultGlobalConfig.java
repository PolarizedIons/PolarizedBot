package net.polarizedions.polarizedbot.config.defaults;

import com.google.gson.InstanceCreator;
import net.polarizedions.polarizedbot.config.GlobalConfig;

import java.lang.reflect.Type;

public class DefaultGlobalConfig implements InstanceCreator<GlobalConfig> {
    @Override
    public GlobalConfig createInstance(Type type) {
        GlobalConfig defaultConfig = new GlobalConfig();
        defaultConfig.owner = "153583013974900738";  // PolarizedIons
        defaultConfig.wolframAlphaApi = "";
        return defaultConfig;
    }
}
