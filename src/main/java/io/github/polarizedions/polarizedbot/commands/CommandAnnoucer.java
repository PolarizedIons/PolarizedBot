package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.announcer.AnnouncerManager;
import io.github.polarizedions.polarizedbot.announcer.IAnnouncer;
import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
import sx.blah.discord.handle.obj.IChannel;

import java.util.function.BiConsumer;

public class CommandAnnoucer implements ICommand {
    @Override
    public String getCommand() {
        return "announce";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public UserRank getRequiredRank() {
        return UserRank.GUILD_ADMIN;
    }

    @Override
    public void exec(CommandMessage command) {
        if (command.getArgs().length == 0) {
            command.replyLocalized("command.announce.error.no_subcommand", "subscribe, unsubscribe, list");
            return;
        }

        String subcommand = command.getArgs()[0];
        switch (subcommand) {
            case "subscribe":
            case "sub":
                this.subscribe(command);
                break;
            case "unsubscribe":
            case "unsub":
                this.unsubscribe(command);
                break;
            case "list":
                this.listAnnouncements(command);
                break;
            default:
                command.replyLocalized("command.announce.error.unknown_subcommand", subcommand, "subscribe, unsubscribe, list");
        }
    }

    private void listAnnouncements(CommandMessage command) {
        AnnouncerManager announcerManager = Bot.instance.getAnnouncerManager();
        command.replyLocalized("command.announce.list", String.join(", ", announcerManager.getNames()));
    }

    private void subscribe(CommandMessage command) {
        this.manageSubscription(command, true);
    }

    private void unsubscribe(CommandMessage command) {
        this.manageSubscription(command, false);
    }

    private void manageSubscription(CommandMessage command, boolean isSub) {
        AnnouncerManager announcerManager = Bot.instance.getAnnouncerManager();

        if (command.getArgs().length < 2) {
            command.replyLocalized("command.announce.error.no_name_provided");
            return;
        }

        IAnnouncer announcer = announcerManager.getAnnouncer(command.getArgs()[1]);
        if (announcer == null) {
            command.replyLocalized("command.announce.error.no_such_announcement");
            return;
        }

        IChannel announceChannel = command.getChannel();
        if (command.getArgs().length > 2) {
            String channelArg = command.getArgs()[2];
            if (channelArg.startsWith("<#")) {
                for (IChannel mentionedChannel : command.getWrappedMessage().getChannelMentions()) {
                    if (mentionedChannel.toString().equals(channelArg)) {
                        announceChannel = mentionedChannel;
                        break;
                    }
                }
            }
        }

        BiConsumer<IAnnouncer, IChannel> method = isSub ? announcerManager::addSub : announcerManager::forgetSub;
        method.accept(announcer, announceChannel);
        command.replyLocalized("command.announce.success." + (isSub ? "sub" : "unsub"), announcer.getName(), announceChannel.toString());
    }
}
