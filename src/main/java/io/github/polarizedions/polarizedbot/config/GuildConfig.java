package io.github.polarizedions.polarizedbot.config;

import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildConfig {
    public String lang;
    public String commandPrefix;
    public Map<String, UserRank> rank = new HashMap<>();
    public List<String> disabledCommands = new ArrayList<>();

    public UserRank getUserRank(User user) {
        return this.rank.getOrDefault(user.getId(), UserRank.DEFAULT);
    }

    public void setRank(User user, UserRank rank) {
        if (rank == UserRank.DEFAULT) {
            this.rank.remove(user.getId());
        }
        else {
            this.rank.put(user.getId(), rank);
        }
    }
}
