package net.polarizedions.polarizedbot.config.defaults;

import net.polarizedions.polarizedbot.config.GuildConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultGuildConfig extends GuildConfig {
    {
        lang = "en";
        commandPrefix = "!";
        disabledCommands = new ArrayList<>();
        ignoredUsers = new ArrayList<>();
        rank = new HashMap<>();
        disabledResponders = new ArrayList<>();
    }
}
