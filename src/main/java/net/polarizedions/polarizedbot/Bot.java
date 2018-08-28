package net.polarizedions.polarizedbot;

import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.autoresponders.ResponderManager;
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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Properties;

public class Bot {
    public static final Logger logger = LogManager.getLogger("PolarizedBot");
    public static Bot instance;
    private static Instant startInstant;
    private static String version;
    private static String buildDate;
    private Instant connectedInstant;

    private IDiscordClient client;
    private AnnouncerManager announcerManager;
    private CommandManager commandManager;
    private ResponderManager responderManager;

    Bot() {
        logger.info("Starting bot {}...", Bot.getVersion());
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
        this.responderManager = new ResponderManager();
    }

    private void run() {
        this.client = createClient();

        this.client.getDispatcher().registerListener((IListener<ReadyEvent>) readyEvent -> {
            this.connectedInstant = Instant.now();
            this.commandManager.registerListeners(this.client);
            this.responderManager.registerListeners(this.client);
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
            throw new RuntimeException("'BOT_TOKEN' environment variable MUST be set!");
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

    public ResponderManager getResponderManager() {
        return this.responderManager;
    }

    public Instant getStartInstant() {
        return Bot.startInstant;
    }

    public Instant getConnectedInstant() {
        return this.connectedInstant;
    }

    public static String getVersion() {
        if (Bot.version == null || Bot.buildDate == null) {
            Properties versionProp = new Properties();
            try {
                versionProp.load(Bot.class.getResourceAsStream("/version.txt"));
                Bot.version = "v" + versionProp.getProperty("version");
                Bot.buildDate = versionProp.getProperty("time");
            } catch (IOException e) {
                Bot.version = "unknown";
                Bot.buildDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(Instant.now());
            }
        }

        return Bot.version + " / " + Bot.buildDate;
    }

    public static void main(String[] args) {
        Bot.startInstant = Instant.now();
        new Bot().run();
    }
}
