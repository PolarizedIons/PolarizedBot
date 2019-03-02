package net.polarizedions.polarizedbot.config;

import net.polarizedions.polarizedbot.util.UserRank;

import java.util.List;
import java.util.Map;

public class GuildConfig {
    public String lang;
    public String commandPrefix;
    public Map<Long, UserRank> rank;
    public List<String> disabledCommands;
    public List<Long> ignoredUsers;
    public List<String> disabledResponders;
}
