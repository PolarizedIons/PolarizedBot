package net.polarizedions.polarizedbot;

import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.shard.LoginEvent;
import sx.blah.discord.handle.impl.events.shard.ReconnectSuccessEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.time.Instant;

public class EventListener {
    private static Logger messageReceivedLogger = LogManager.getLogger("MessageReceiver");


    @EventSubscriber
    public void onReady(ReadyEvent event) {
        IUser us = event.getClient().getOurUser();
        Bot.logger.info("Ready to go as {} ({})!", us.getName() + "#" + us.getDiscriminator(), us.getStringID());

        if (Bot.instance.announcerManager != null) {
            Bot.instance.announcerManager.stop();
        }
        Bot.instance.announcerManager = new AnnouncerManager();
        Bot.instance.announcerManager.load();
        Bot.instance.announcerManager.initAnnouncers();
        Bot.instance.presenceUtil.init();
    }

    @EventSubscriber
    public void onLogin(LoginEvent event) {
        Bot.instance.connectedInstant = Instant.now();
    }

    @EventSubscriber
    public void onReconnected(ReconnectSuccessEvent event) {
        Bot.instance.connectedInstant = Instant.now();
    }

    @EventSubscriber
    public void onGuildCreated(GuildCreateEvent event) {
        IGuild guild = event.getGuild();
        if (! GuildManager.userHasRank(guild, guild.getOwner(), UserRank.LOCAL_ADMIN)) {
            Bot.logger.debug("Set " + guild.getOwner() + " as local admin for guild: " + guild);
            GuildManager.setRank(guild, guild.getOwner(), UserRank.LOCAL_ADMIN);
        }
    }

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        messageReceivedLogger.info("[UserID: {}, GuildID: {}, ChannelID: {}, MessageID: {}] {}: {}",
                message.getAuthor().getStringID(),
                message.getGuild() == null ? "PM" : message.getGuild().getStringID(),
                message.getChannel().getStringID(),
                message.getStringID(),
                message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator(),
                message.getContent()
        );

        Bot.instance.threadPool.execute(() -> Bot.instance.commandManager.messageHandler(message));
        Bot.instance.threadPool.execute(() -> Bot.instance.responderManager.messageHandler(message));
    }
}
