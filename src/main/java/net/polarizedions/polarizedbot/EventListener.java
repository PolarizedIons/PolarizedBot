package net.polarizedions.polarizedbot;

import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public class EventListener {
    private static Logger messageReceivedLogger = LogManager.getLogger("MessageReceiver");

    private Bot bot;
    public EventListener(Bot bot) {
        this.bot = bot;
    }

    public void onReady(ReadyEvent event) {
        User us = event.getClient().getSelf().block();
        Bot.logger.info("Ready to go as {} ({})!", us.getUsername() + "#" + us.getDiscriminator(), us.getId().asString());

        if (this.bot.announcerManager != null) {
            this.bot.announcerManager.stop();
        }
        this.bot.announcerManager = new AnnouncerManager(this.bot);
        this.bot.announcerManager.load();
        this.bot.announcerManager.initAnnouncers();

        this.bot.presenceUtil.init(event.getClient());

        this.bot.connectedInstant = Instant.now();
    }

    public void onReconnected(ReconnectEvent event) {
        this.bot.connectedInstant = Instant.now();
    }

    public void onGuildCreated(GuildCreateEvent event) {
        Guild guild = event.getGuild();
        User guildOwner = guild.getOwner().block();
        if (! this.bot.getGuildManager().userHasRank(guild, guildOwner, UserRank.LOCAL_ADMIN)) {
            Bot.logger.debug("Set " + guild.getOwner() + " as local admin for guild: " + guild);
            this.bot.getGuildManager().setRank(guild, guildOwner, UserRank.LOCAL_ADMIN);
        }
    }

    public void onMessageReceived(MessageCreateEvent event) {
        Message message = event.getMessage();
        User user = message.getAuthor().isPresent() ? message.getAuthor().get() : null;
        event.getGuild().subscribe(guild ->
                event.getMessage().getChannel().subscribe(channel -> {
                    User author = message.getAuthor().isPresent() ? message.getAuthor().get() : null;
                    messageReceivedLogger.info("[UserID: {}, GuildID: {}, ChannelID: {}, MessageID: {}] {}: {}",
                            author == null ? "null" : author.getId().asLong(),
                            guild == null ? "PM" : guild.getId().asString(),
                            channel.getId().asString(),
                            message.getId().asString(),
                            author.getUsername() + "#" + author.getDiscriminator(),
                            message.getContent().isPresent() ? message.getContent().get() : ""
                    );

                    this.bot.commandManager.messageHandler(guild, user, channel, message);
                    this.bot.responderManager.messageHandler(guild, user, channel, message);
                })
        );
    }
}
