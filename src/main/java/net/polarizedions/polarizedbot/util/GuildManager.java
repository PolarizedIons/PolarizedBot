package net.polarizedions.polarizedbot.util;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.config.GuildConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuildManager {
    private static Map<Long, GuildConfig> configs;

    public static void init() {
        configs = new HashMap<>();
    }

    public static GuildConfig getConfig(@NotNull Guild guild) {
        configs.computeIfAbsent(guild.getId().asLong(), id -> {
            try {
                return ConfigManager.loadGuildConfig(id);
            }
            catch (IOException e) {
                return null;
            }
        });

        return configs.get(guild.getId().asLong());
    }

    public static void saveConfig(Guild guild) {
        try {
            ConfigManager.saveGuildConfig(guild, getConfig(guild));
        }
        catch (IOException e) {
            // NOOP
        }
    }

    public static UserRank getUserRank(Guild guild, User user) {
        if (Bot.instance.getGlobalConfig().globalAdmins.contains(user.getId().asLong())) {
            return UserRank.GLOBAL_ADMIN;
        }

        return getConfig(guild).rank.getOrDefault(user.getId().asLong(), UserRank.DEFAULT);
    }

    public static void setRank(Guild guild, User user, UserRank rank) {
        if (rank == UserRank.DEFAULT) {
            getConfig(guild).rank.remove(user.getId().asLong());
        }
        else {
            getConfig(guild).rank.put(user.getId().asLong(), rank);
        }
        saveConfig(guild);
    }

    public static boolean userHasRank(Message message, UserRank requiredRank) {
        return message.getAuthor().isPresent() && userHasRank(message.getGuild().block(), message.getAuthor().get(), requiredRank);
    }

    public static boolean userHasRank(Guild guild, User user, UserRank requiredRank) {
        if (guild == null) {
            return requiredRank == UserRank.DEFAULT ||
                    ( requiredRank == UserRank.GLOBAL_ADMIN &&
                            Bot.instance.getGlobalConfig().globalAdmins.contains(user.getId().asLong())
                    );
        }

        return getUserRank(guild, user).rank >= requiredRank.rank;
    }
}
