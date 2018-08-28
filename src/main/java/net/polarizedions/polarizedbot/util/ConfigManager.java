package net.polarizedions.polarizedbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.config.defaults.DefaultGlobalConfig;
import net.polarizedions.polarizedbot.config.defaults.DefaultGuildConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.obj.IGuild;

import java.io.*;
import java.nio.file.Paths;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger("ConfigManager");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GlobalConfig.class, new DefaultGlobalConfig())
            .registerTypeAdapter(GuildConfig.class, new DefaultGuildConfig())
            .setPrettyPrinting()
            .create();

    public static File configDir = new File("config");
    private static GlobalConfig globalConfig;

    public static void loadGlobalConfig() throws IOException {
        String configDirStr = configDir.getAbsolutePath();
        logger.info("Loading config from the directory: {}", configDirStr);
        if (!configDir.exists()) {
            logger.debug("Creating config folder");
            configDir.mkdir();
        }

        File globalFile = Paths.get(configDirStr, "bot.json").toFile();
        globalFile.createNewFile();
        logger.info("Loading global config from: {}", globalFile);
        Reader reader = new FileReader(globalFile);
        globalConfig = GSON.fromJson(reader, GlobalConfig.class);

        if (globalConfig == null) {
            globalConfig = GSON.fromJson("{}", GlobalConfig.class);
        }

        Writer writer = new FileWriter(globalFile);
        GSON.toJson(globalConfig, writer);
        writer.close();
    }

    public static GuildConfig loadGuildConfig(Long guildId) throws IOException {
        File guildsFolder = Paths.get(configDir.getAbsolutePath(), "guilds").toFile();
        if (!guildsFolder.exists()) {
            logger.debug("Creating guilds config folder");
            guildsFolder.mkdir();
        }

        File guildFile = Paths.get(guildsFolder.toString(), guildId + ".json").toFile();
        guildFile.createNewFile();

        logger.info("Loading Guild Config from: {}", guildFile);

        GuildConfig guildConfig = GSON.fromJson(new FileReader(guildFile), GuildConfig.class);

        if (guildConfig == null) {
            guildConfig = GSON.fromJson("{}", GuildConfig.class);
        }
        Writer writer = new FileWriter(guildFile);
        GSON.toJson(guildConfig, writer);
        writer.close();

        return guildConfig;
    }

    public static void saveGuildConfig (IGuild guild, GuildConfig config) throws IOException {
        File guildsFolder = Paths.get(configDir.getAbsolutePath(), "guilds").toFile();
        if (!guildsFolder.exists()) {
            logger.debug("Creating guilds config folder");
            guildsFolder.mkdir();
        }

        File guildFile = Paths.get(guildsFolder.toString(), guild.getLongID() + ".json").toFile();

        if (config == null) {
            logger.error("Tried to save unknown guild config!?");
            return;
        }

        logger.info("Saving Guild Config file: {}", guildFile);
        Writer writer = new FileWriter(guildFile);
        GSON.toJson(config, writer);
        writer.close();
    }

    public static GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
}
