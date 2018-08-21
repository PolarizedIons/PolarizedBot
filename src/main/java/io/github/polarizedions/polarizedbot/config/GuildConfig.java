package io.github.polarizedions.polarizedbot.config;

import io.github.polarizedions.polarizedbot.util.UserRank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildConfig {
    public String lang;
    public String commandPrefix;
    public Map<Long, UserRank> rank = new HashMap<>();
    public List<String> disabledCommands = new ArrayList<>();
    public List<Long> ignoredUsers;
}
