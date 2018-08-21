package net.polarizedions.polarizedbot.util;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.config.defaults.DefaultGlobalConfig;
import net.polarizedions.polarizedbot.config.defaults.DefaultGuildConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger("ConfigManager");

    public static File configDir = new File("config");
    private static GlobalConfig globalConfig;

    private static TomlWriter tomlWriter = new TomlWriter();

    // THIS IS UGLY I KNOW!
    private static Toml defaultGlobal = new Toml().read(tomlWriter.write(new DefaultGlobalConfig()));
    private static Toml defaultGuild = new Toml().read(tomlWriter.write(new DefaultGuildConfig()));

    public static void loadGlobalConfig() throws IOException {
        String configDirStr = configDir.getAbsolutePath();
        logger.info("Loading config from the directory: {}", configDirStr);
        if (!configDir.exists()) {
            logger.debug("Creating config folder");
            configDir.mkdir();
        }

        File globalFile = Paths.get(configDirStr, "bot.toml").toFile();
        globalFile.createNewFile();
        logger.info("Loading global config from: {}", globalFile);
        Toml globalToml = new Toml(defaultGlobal).read(globalFile);
        globalConfig = globalToml.to(GlobalConfig.class);
        tomlWriter.write(globalConfig, globalFile);
    }

    public static GuildConfig loadGuildConfig(Long guildId) throws IOException {
        File guildsFolder = Paths.get(configDir.getAbsolutePath(), "guilds").toFile();
        if (!guildsFolder.exists()) {
            logger.debug("Creating guilds config folder");
            guildsFolder.mkdir();
        }

        File guildFile = Paths.get(guildsFolder.toString(), guildId + ".toml").toFile();
        guildFile.createNewFile();

        logger.info("Loading Guild Config from: {}", guildFile);
        Toml guildToml = new Toml(defaultGuild).read(guildFile);
        GuildConfig guildConfig = guildToml.to(GuildConfig.class);
        tomlWriter.write(guildConfig, guildFile);

        return guildConfig;
    }

    public static void saveGuildConfig (IGuild guild, GuildConfig config) throws IOException {
        File guildsFolder = Paths.get(configDir.getAbsolutePath(), "guilds").toFile();
        if (!guildsFolder.exists()) {
            logger.debug("Creating guilds config folder");
            guildsFolder.mkdir();
        }

        File guildFile = Paths.get(guildsFolder.toString(), guild.getLongID() + ".toml").toFile();

        if (config == null) {
            logger.error("Tried to save unknown guild config!?");
            return;
        }

        logger.info("Saving Guild Config file: {}", guildFile);
        tomlWriter.write(config, guildFile);
    }

    public static GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
}
