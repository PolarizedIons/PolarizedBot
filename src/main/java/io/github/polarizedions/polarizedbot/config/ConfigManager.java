package io.github.polarizedions.polarizedbot.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import io.github.polarizedions.polarizedbot.config.defaults.DefaultGlobalConfig;
import io.github.polarizedions.polarizedbot.config.defaults.DefaultGuildConfig;
import io.github.polarizedions.polarizedbot.wrappers.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger("ConfigManager");

    public static File configDir = new File("config");
    private GlobalConfig globalConfig;
    private HashMap<String, GuildConfig> guildConfigs;

    private TomlWriter tomlWriter = new TomlWriter();
    private Toml defaultGlobal;
    private Toml defaultGuild;

    public ConfigManager() {
        this.guildConfigs = new HashMap<>();

        // THIS IS UGLY I KNOW!
        defaultGlobal = new Toml().read(tomlWriter.write(new DefaultGlobalConfig()));
        defaultGuild = new Toml().read(tomlWriter.write(new DefaultGuildConfig()));
    }

    public ConfigManager load() throws IOException {
        String configDirStr = configDir.getAbsolutePath();
        logger.info("Loading config from the directory: {}", configDirStr);
        if (!configDir.exists()) {
            logger.debug("Creating config folder");
            configDir.mkdir();
        }

        File guildsFolder = Paths.get(configDirStr, "guilds").toFile();
        if (!guildsFolder.exists()) {
            logger.debug("Creating guilds config folder");
            guildsFolder.mkdir();
        }

        File globalFile = Paths.get(configDirStr, "bot.toml").toFile();
        globalFile.createNewFile();
        logger.info("Loading global config from: {}", globalFile);
        Toml globalToml = new Toml(defaultGlobal).read(globalFile);
        globalConfig = globalToml.to(GlobalConfig.class);
        tomlWriter.write(globalConfig, globalFile);

        return this;
    }

    public GuildConfig loadGuildConfig(String guildId) throws IOException {
        File guildConfigFile = Paths.get(configDir.getAbsolutePath(), "guilds", guildId + ".toml").toFile();
        guildConfigFile.createNewFile();

        logger.info("Loading Guild Config from: {}", guildConfigFile);
        Toml guildConfigToml = new Toml(defaultGuild).read(guildConfigFile);
        GuildConfig guildConfig = guildConfigToml.to(GuildConfig.class);
        tomlWriter.write(guildConfig, guildConfigFile);

        return guildConfig;
    }

    public GuildConfig getConfigForGuild(Guild guild) {
        if (!guildConfigs.containsKey(guild.getId())) {
            try {
                guildConfigs.put(guild.getId(), loadGuildConfig(guild.getId()));
            }
            catch (IOException e) {
                logger.error("Failed to load config for guild {} ()", guild.getId(), guild.getName());
            }
        }

        return guildConfigs.get(guild.getId());
    }

    public void saveConfigForGuild(Guild guild) throws IOException {
        File guildConfigFile = Paths.get(configDir.getAbsolutePath(), "guilds", guild.getId() + ".toml").toFile();

        GuildConfig guildConfig = guildConfigs.get(guild.getId());
        if (guildConfig == null) {
            logger.error("Tried to save unknown guild config!?");
            return;
        }

        logger.info("Saving Guild Config file: {}", guildConfigFile);
        tomlWriter.write(guildConfig, guildConfigFile);
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
}
