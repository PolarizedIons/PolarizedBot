package net.polarizedions.polarizedbot;

import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.commands.CommandManager;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.util.ConfigManager;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
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

    private Bot() {
        logger.info("Starting bot...");
        instance = this;

        try {
            ConfigManager.loadGlobalConfig();
        }
        catch (IOException e) {
            logger.error("Error loading config", e);
            System.exit(1);
        }

        Localizer.init();
        GuildManager.init();
        this.announcerManager = new AnnouncerManager();
        this.commandManager = new CommandManager();
    }

    private void run() {
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

    public void softRestart() {
        Thread thread = new Thread(() -> {
            this.shutdown();
            new Bot().run();
        }, "restart thread");
        thread.setDaemon(false);
        thread.start();
    }

    public GlobalConfig getGlobalConfig() {
        return ConfigManager.getGlobalConfig();
    }

    public IDiscordClient getClient() {
        return client;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AnnouncerManager getAnnouncerManager() {
        return announcerManager;
    }

    public static void main(String[] args) {
        new Bot().run();
    }
}
