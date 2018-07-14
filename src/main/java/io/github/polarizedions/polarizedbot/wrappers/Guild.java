package io.github.polarizedions.polarizedbot.wrappers;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.config.GuildConfig;
import io.github.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IGuild;

import java.io.IOException;


public class Guild {
    private IGuild wrappedGuild;
    private GuildConfig config;

    public Guild(IGuild wrappingGuild) {
        this.wrappedGuild = wrappingGuild;
        this.config = Bot.instance.getConfigForGuild(this);
    }

    public IGuild getWrappedGuild() {
        return wrappedGuild;
    }

    public UserRank getUserRank(User user) {
        String ownerId = Bot.instance.getGlobalConfig().owner;
        if (ownerId.equals(user.getId())) {
            return UserRank.BOT_OWNER;
        }

        return config.getUserRank(user);
    }

    public GuildConfig getConfig() {
        return config;
    }

    public String getId() {
        return wrappedGuild.getStringID();
    }

    public String getName() {
        return wrappedGuild.getName();
    }

    @Override
    public String toString() {
        return "Guild[Name: " + getName() + ", ID:" + getId() + "]";
    }

    public void saveConfig() {
        try {
            Bot.instance.getConfigManager().saveConfigForGuild(this);
        } catch (IOException e) {
            Bot.logger.error("Error saving guild data!", e);
        }
    }
}
