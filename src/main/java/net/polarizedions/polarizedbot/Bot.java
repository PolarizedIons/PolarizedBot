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
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Properties;

public class Bot {
    public static final Logger logger = LogManager.getLogger("PolarizedBot");
    public static Bot instance;
    private static Instant startInstant;
    public static String version;
    public static String buildDate;
    private Instant connectedInstant;

    private IDiscordClient client;
    private AnnouncerManager announcerManager;
    private CommandManager commandManager;
    private ResponderManager responderManager;

    Bot() {
        logger.info("Starting bot {}...", Bot.getFullVersion());
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
            this.announcerManager.load();
            this.announcerManager.initAnnouncers();
        });

        this.client.getDispatcher().registerListener((IListener<MessageReceivedEvent>) messageEvent -> {
            IMessage message = messageEvent.getMessage();
            logger.debug("[UserID: {}, GuildID: {}, MessageID: {}, User: {}]: {}",
                    message.getAuthor().getStringID(),
                    message.getGuild() == null ? "  0  " : message.getGuild().getStringID(),
                    message.getStringID(),
                    message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator(),
                    message.getContent()
            );

            this.commandManager.messageHandler(message);
            this.responderManager.messageHandler(message);
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

    public static String getFullVersion() {
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
        for (int i = 0; i < args.length; i++) {
            // Delete old jar after update
            if (args[i].equals("--update")) {
                i++;
                File oldJar = new File(args[i]);
                if (!oldJar.delete()) {
                    logger.info("Error deleting old jar {} after update!", args[i]);
                }
                else {
                    logger.info("Deleted old jar {} because of update", args[i]);
                }
            }
        }

        // This is needed because even if I call ProcessBuilder#inheritIO, it doesn't seem to pass
        // SIGINT to the child process, and thus soft-restarting, or updating the bot, cannot be
        // killed by just pressing Control+C.
        new Thread(() -> {
            try {
                while (System.in.read() != -1) { /* NOOP */ }
            }
            catch (IOException e) { /* NOOP */ }

            System.out.println("Exiting because System.in is dead");
            System.exit(0);
        }, "SIGINT catcher").start();

        Bot.startInstant = Instant.now();
        new Bot().run();
    }
}
