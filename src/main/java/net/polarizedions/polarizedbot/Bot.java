package net.polarizedions.polarizedbot;

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
import net.polarizedions.polarizedbot.util.ReactionListener;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {
    public static final Logger logger = LogManager.getLogger("PolarizedBot");
    public static Bot instance;
    public final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private static Instant startInstant;
    private Instant connectedInstant;

    private IDiscordClient client;
    private AnnouncerManager announcerManager;
    private CommandManager commandManager;
    private ResponderManager responderManager;
    private PresenceUtil presenceUtil;
    private ReactionListener reactionListener;

    private Bot() {
        logger.info("Starting bot v{} ({})...", BotInfo.version, BotInfo.buildtime);
        instance = this;

        try {
            ConfigManager.loadGlobalConfig();
        }
        catch (IOException ex) {
            throw new RuntimeException("Cannot load config!", ex);
        }

        Localizer.init();
        GuildManager.init();
        this.commandManager = new CommandManager();
        this.responderManager = new ResponderManager();
        this.presenceUtil = new PresenceUtil(this.getGlobalConfig().presenceStrings, this.getGlobalConfig().presenceDelay * 1000);
    }

    public static void main(String[] args) {
        Args.handle(args);
        Bot.startInstant = Instant.now();
        new Bot().run();
    }

    private void run() {
        this.client = createClient();

        this.reactionListener = new ReactionListener(this.client);

        this.client.getDispatcher().registerListener((IListener<ReadyEvent>)readyEvent -> {
            this.connectedInstant = Instant.now();
            logger.info("Ready to go!");

            if (this.announcerManager != null) {
                this.announcerManager.stop();
            }
            this.announcerManager = new AnnouncerManager();
            this.announcerManager.load();
            this.announcerManager.initAnnouncers();
            this.presenceUtil.init();
        });

        this.client.getDispatcher().registerListener((IListener<GuildCreateEvent>) guildCreatedEvent -> {
            IGuild guild = guildCreatedEvent.getGuild();
            if (! GuildManager.userHasRank(guild, guild.getOwner(), UserRank.GUILD_ADMIN)) {
                logger.debug("Set " + guild.getOwner() + " as guild owner for " + guild);
                GuildManager.setRank(guild, guild.getOwner(), UserRank.GUILD_ADMIN);
            }
        });

        this.client.getDispatcher().registerListener((IListener<MessageReceivedEvent>) messageEvent -> {
            IMessage message = messageEvent.getMessage();
            logger.info("[UserID: {}, GuildID: {}, MessageID: {}, User: {}]: {}",
                    message.getAuthor().getStringID(),
                    message.getGuild() == null ? "PM" : message.getGuild().getStringID(),
                    message.getStringID(),
                    message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator(),
                    message.getContent()
            );

            this.threadPool.execute(() -> this.commandManager.messageHandler(message));
            this.threadPool.execute(() -> this.responderManager.messageHandler(message));
        });
    }

    public IDiscordClient createClient() {
        return createClient(true);
    }

    public IDiscordClient createClient(boolean login) {
        GlobalConfig config = this.getGlobalConfig();
        if (config.owner.isEmpty()) {
            logger.error("The `botowner` value in bot.json MUST NOT be empty!");
        }

        if (config.botToken.isEmpty()) {
            logger.error("The `botToken` value in bot.json MUST NOT be empty!");
        }

        if (config.owner.isEmpty() || config.botToken.isEmpty()) {
            logger.error("Please enter the required value(s) in the config!");
            System.exit(1);
        }

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(config.botToken);
        try {
            if (login) {
                return clientBuilder.login();
            }
            else {
                return clientBuilder.build();
            }
        }
        catch (DiscordException ex) {
            throw new RuntimeException("Error creating/logging in client", ex);
        }
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

    public ReactionListener getReactionListener() {
        return this.reactionListener;
    }
}
