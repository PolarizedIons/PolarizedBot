package io.github.polarizedions.polarizedbot;

import io.github.polarizedions.polarizedbot.announcer.AnnouncerManager;
import io.github.polarizedions.polarizedbot.commands.CommandManager;
import io.github.polarizedions.polarizedbot.config.ConfigManager;
import io.github.polarizedions.polarizedbot.config.GlobalConfig;
import io.github.polarizedions.polarizedbot.config.GuildConfig;
import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.wrappers.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;

public class Bot {
    public static Bot instance;
    public static final Logger logger = LogManager.getLogger("PolarizedBot");;

    private IDiscordClient client;
    private AnnouncerManager announcerManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private Localizer localizer;

    private Bot() {
        logger.info("Starting bot...");
        instance = this;

        try {
            this.configManager = new ConfigManager().load();
        }
        catch (IOException e) {
            logger.error("Error loading config", e);
            System.exit(1);
        }

        this.announcerManager = new AnnouncerManager();
        this.commandManager = new CommandManager();
        this.localizer = new Localizer();
    }

    public void run() {
        this.client = createClient();

        this.client.getDispatcher().registerListener((IListener<ReadyEvent>) readyEvent -> {
            this.commandManager.registerListeners(this.client);
            this.announcerManager.load();
            this.announcerManager.initAnnouncers();
        });
    }

    public IDiscordClient createClient() {
        return createClient(true);
    }

    public IDiscordClient createClient(boolean login) {
        String BOT_TOKEN = System.getenv("BOT_TOKEN");
        if (BOT_TOKEN == null) {
            logger.error("'BOT_TOKEN' environment variable MUST be set!");
            System.exit(1);
        }

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(BOT_TOKEN);
        try {
            if (login) {
                return clientBuilder.login();
            } else {
                return clientBuilder.build();
            }
        }
        catch (DiscordException e) {
            logger.error("Error creating/logging in client", e);
            System.exit(1);
        }

        // THIS SHOULDN'T HAPPEN REALLY
        return null;
    }

    public void shutdown() {
        this.announcerManager.stop();
        this.client.logout();
    }

    public GlobalConfig getGlobalConfig() {
        return configManager.getGlobalConfig();
    }

    public GuildConfig getConfigForGuild(Guild guild) {
        return configManager.getConfigForGuild(guild);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public IDiscordClient getClient() {
        return client;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Localizer getLocalizer() {
        return localizer;
    }

    public AnnouncerManager getAnnouncerManager() {
        return announcerManager;
    }

    public static void main(String[] args) {
        new Bot().run();
    }
}
