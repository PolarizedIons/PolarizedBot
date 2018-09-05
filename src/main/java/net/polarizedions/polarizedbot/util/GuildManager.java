package net.polarizedions.polarizedbot.util;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.config.GuildConfig;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuildManager {
    private static Map<Long, GuildConfig> configs;

    public static void init() {
        configs = new HashMap<>();
    }

    public static GuildConfig getConfig(@NotNull IGuild guild) {
        configs.computeIfAbsent(guild.getLongID(), id -> {
            try {
                return ConfigManager.loadGuildConfig(id);
            }
            catch (IOException e) {
                return null;
            }
        });

        return configs.get(guild.getLongID());
    }

    public static void saveConfig(IGuild guild) {
        try {
            ConfigManager.saveGuildConfig(guild, getConfig(guild));
        }
        catch (IOException e) {
            // NOOP
        }
    }

    public static UserRank getUserRank(IGuild guild, IUser user) {
        if (Bot.instance.getGlobalConfig().owner.equals(user.getStringID())) {
            return UserRank.BOT_OWNER;
        }

        return getConfig(guild).rank.getOrDefault(user.getLongID(), UserRank.DEFAULT);
    }

    public static void setRank(IGuild guild, IUser user, UserRank rank) {
        if (rank == UserRank.DEFAULT) {
            getConfig(guild).rank.remove(user.getLongID());
        }
        else {
            getConfig(guild).rank.put(user.getLongID(), rank);
        }
        saveConfig(guild);
    }

    public static boolean userHasRank(IMessage message, UserRank requiredRank) {
        return userHasRank(message.getGuild(), message.getAuthor(), requiredRank);
    }

    public static boolean userHasRank(IGuild guild, IUser user, UserRank requiredRank) {
        if (guild == null) {
            return requiredRank == UserRank.DEFAULT || ( requiredRank == UserRank.BOT_OWNER && Bot.instance.getGlobalConfig().owner.equals(user.getStringID()) );
        }

        return getUserRank(guild, user).rank >= requiredRank.rank;
    }
}
