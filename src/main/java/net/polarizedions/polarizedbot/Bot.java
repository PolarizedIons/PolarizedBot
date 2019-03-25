package net.polarizedions.polarizedbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.autoresponders.ResponderManager;
import net.polarizedions.polarizedbot.commands.CommandManager;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.util.Args;
import net.polarizedions.polarizedbot.util.BotInfo;
import net.polarizedions.polarizedbot.util.ConfigManager;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.PresenceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    public static final Logger logger = LogManager.getLogger("PolarizedBot");
    public final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 8));
    private static Instant startInstant;
    Instant connectedInstant;

    private DiscordClient client;
    AnnouncerManager announcerManager;
    CommandManager commandManager;
    GuildManager guildManager;
    ResponderManager responderManager;
    PresenceUtil presenceUtil;
//    ReactionListener reactionListener;

    private Bot() {
        logger.info("Starting bot v{} ({})...", BotInfo.version, BotInfo.buildtime);

        try {
            ConfigManager.loadGlobalConfig();
        }
        catch (IOException ex) {
            throw new RuntimeException("Cannot load config!", ex);
        }

        Localizer.init();
        this.guildManager = new GuildManager(this);
        this.commandManager = new CommandManager(this);
        this.responderManager = new ResponderManager(this);
        this.presenceUtil = new PresenceUtil(this, this.getGlobalConfig().presenceStrings, this.getGlobalConfig().presenceDelay * 1000);
    }

    public static void main(String[] args) {
        Args.handle(args);
        Bot.startInstant = Instant.now();
        new Bot().run();
    }

    private void run() {
        this.client = createClient();

//        this.reactionListener = new ReactionListener(this.client);
        logger.debug("Subscribing to events");
        EventListener listener = new EventListener(this);
        EventDispatcher events = this.client.getEventDispatcher();

        events.on(ReadyEvent.class).subscribe(listener::onReady);
        events.on(ReconnectEvent.class).subscribe(listener::onReconnected);
        events.on(GuildCreateEvent.class).subscribe(listener::onGuildCreated);
        events.on(MessageCreateEvent.class).subscribe(listener::onMessageReceived);

        logger.info("Logging in");
        client.login().block();
    }

    public DiscordClient createClient() {
        GlobalConfig config = this.getGlobalConfig();
        if (config.globalAdmins.isEmpty()) {
            logger.error("The `globalAdmins` value in bot.json MUST NOT be empty!");
        }

        if (config.botToken.isEmpty()) {
            logger.error("The `botToken` value in bot.json MUST NOT be empty!");
        }

        if (config.globalAdmins.isEmpty() || config.botToken.isEmpty()) {
            logger.error("Please enter the required value(s) in the config!");
            System.exit(1);
        }

        logger.debug("Creating client");
        this.client = new DiscordClientBuilder(config.botToken).build();
        return this.client;
    }

    public void shutdown() {
        this.threadPool.shutdownNow();
        this.announcerManager.stop();
        this.presenceUtil.stop();
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

    public DiscordClient getClient() {
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

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public Instant getStartInstant() {
        return Bot.startInstant;
    }

    public Instant getConnectedInstant() {
        return this.connectedInstant;
    }

//    public ReactionListener getReactionListener() {
//        return this.reactionListener;
//    }
}
