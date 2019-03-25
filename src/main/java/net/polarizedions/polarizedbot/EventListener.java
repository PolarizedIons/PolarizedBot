package net.polarizedions.polarizedbot;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public class EventListener {
    private static Logger messageReceivedLogger = LogManager.getLogger("MessageReceiver");

    private DiscordClient client;
    public EventListener(DiscordClient client) {
        this.client = client;
    }

    public void onReady(ReadyEvent event) {
        User us = event.getClient().getSelf().block();
        Bot.logger.info("Ready to go as {} ({})!", us.getUsername() + "#" + us.getDiscriminator(), us.getId().asString());

        if (Bot.instance.announcerManager != null) {
            Bot.instance.announcerManager.stop();
        }
        Bot.instance.announcerManager = new AnnouncerManager();
        Bot.instance.announcerManager.load();
        Bot.instance.announcerManager.initAnnouncers();
        Bot.instance.presenceUtil.init();

        Bot.instance.connectedInstant = Instant.now();
    }

    public void onReconnected(ReconnectEvent event) {
        Bot.instance.connectedInstant = Instant.now();
    }

    public void onGuildCreated(GuildCreateEvent event) {
        Guild guild = event.getGuild();
        User guildOwner = guild.getOwner().block();
        if (! GuildManager.userHasRank(guild, guildOwner, UserRank.LOCAL_ADMIN)) {
            Bot.logger.debug("Set " + guild.getOwner() + " as local admin for guild: " + guild);
            GuildManager.setRank(guild, guildOwner, UserRank.LOCAL_ADMIN);
        }
    }

    public void onMessageReceived(MessageCreateEvent event) {
        Message message = event.getMessage();
        User user = message.getAuthor().isPresent() ? message.getAuthor().get() : null;
        event.getGuild().subscribe(guild ->
                event.getMessage().getChannel().subscribe(channel -> {

                    User author = message.getAuthor().get();
                    messageReceivedLogger.info("[UserID: {}, GuildID: {}, ChannelID: {}, MessageID: {}] {}: {}",
                            author.getId().asLong(),
                            guild == null ? "PM" : guild.getId().asString(),
                            channel.getId().asString(),
                            message.getId().asString(),
                            author.getUsername() + "#" + author.getDiscriminator(),
                            message.getContent().isPresent() ? message.getContent().get() : ""
                    );

                    Bot.instance.commandManager.messageHandler(guild, user, channel, message);
                    Bot.instance.responderManager.messageHandler(guild, user, channel, message);
                })
        );
    }
}
