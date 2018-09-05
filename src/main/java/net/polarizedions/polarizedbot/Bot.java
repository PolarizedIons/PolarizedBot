package net.polarizedions.polarizedbot;

import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.autoresponders.ResponderManager;
import net.polarizedions.polarizedbot.commands.CommandManager;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.util.Args;
import net.polarizedions.polarizedbot.util.ConfigManager;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Properties;

public class Bot {
    public static final Logger logger = LogManager.getLogger("PolarizedBot");
    public static Bot instance;
    public static String version;
    public static String buildDate;
    private static Instant startInstant;
    private Instant connectedInstant;

    private IDiscordClient client;
    private AnnouncerManager announcerManager;
    private CommandManager commandManager;
    private ResponderManager responderManager;

    private Bot() {
        if (Bot.version == null) {
            Bot.loadBuildInfo();
        }

        logger.info("Starting bot {}...", Bot.getFullVersion());
        instance = this;

        try {
            ConfigManager.loadGlobalConfig();
        }
        catch (IOException ex) {
            throw new RuntimeException("Cannot load config!", ex);
        }

        Localizer.init();
        GuildManager.init();
        this.announcerManager = new AnnouncerManager();
        this.commandManager = new CommandManager();
        this.responderManager = new ResponderManager();
    }

    @NotNull
    @Contract(pure = true)
    public static String getFullVersion() {
        return Bot.version + " / " + Bot.buildDate;
    }

    private static void loadBuildInfo() {
        Properties versionProp = new Properties();
        try {
            versionProp.load(Bot.class.getResourceAsStream("/version.txt"));
            Bot.version = "v" + versionProp.getProperty("version");
            Bot.buildDate = versionProp.getProperty("time");
        }
        catch (IOException e) {
            Bot.version = "unknown";
            Bot.buildDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(Instant.now());
        }
    }

    public static void main(String[] args) {
        Args.handle(args);

        // This is needed because even if I call ProcessBuilder#inheritIO, it doesn't seem to pass
        // SIGINT to the child process, and thus soft-restarting, or updating the bot, cannot be
        // killed by just pressing Control+C.
        new Thread(() -> {
            try {
                //noinspection StatementWithEmptyBody
                while (System.in.read() != -1) { /* NOOP */ }
            }
            catch (IOException e) { /* NOOP */ }

            System.out.println("Exiting because System.in is dead");
            System.exit(0);
        }, "SIGINT catcher").start();

        Bot.startInstant = Instant.now();
        new Bot().run();
    }

    private void run() {
        this.client = createClient();

        this.client.getDispatcher().registerListener((IListener<ReadyEvent>)readyEvent -> {
            this.connectedInstant = Instant.now();
            this.announcerManager.load();
            this.announcerManager.initAnnouncers();
        });

        this.client.getDispatcher().registerListener((IListener<MessageReceivedEvent>)messageEvent -> {
            IMessage message = messageEvent.getMessage();
            logger.debug("[UserID: {}, GuildID: {}, MessageID: {}, User: {}]: {}",
                    message.getAuthor().getStringID(),
                    message.getGuild() == null ? "PM" : message.getGuild().getStringID(),
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
        GlobalConfig config = this.getGlobalConfig();
        if (config.owner.isEmpty()) {
            logger.error("The `botowner` value in bot.json MUST NOT be empty!");
        }

        if (config.botToken.isEmpty()) {
            logger.error("The `botToken` value in bot.json MUST NOT be empty!");
        }

        if (config.owner.isEmpty() || config.botToken.isEmpty()) {
            throw new RuntimeException("Please enter the required value(s) in the config!");
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
}
