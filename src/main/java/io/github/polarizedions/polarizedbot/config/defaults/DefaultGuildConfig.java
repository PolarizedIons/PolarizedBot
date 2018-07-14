package io.github.polarizedions.polarizedbot.config.defaults;

import io.github.polarizedions.polarizedbot.config.GuildConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultGuildConfig extends GuildConfig {
    {
        lang = "en";
        commandPrefix = "!";
        disabledCommands = new ArrayList<>();
        rank = new HashMap<>();
    }
}
