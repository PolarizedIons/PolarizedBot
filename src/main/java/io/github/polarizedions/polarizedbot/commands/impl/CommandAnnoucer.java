package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.announcer.AnnouncerManager;
import io.github.polarizedions.polarizedbot.announcer.IAnnouncer;
import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;
import java.util.function.BiConsumer;

public class CommandAnnoucer implements ICommand {

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Announcer")
                .setRank(UserRank.GUILD_ADMIN)
                .setHelp("announces things")
                .command("announce", announce -> announce
                        .optionArg(new String[]{"subscribe", "sub"}, sub -> sub
                            .captureArg(name -> name
                                .channelArg(channel -> channel
                                        .onExecute((msg, args) -> this.manageSubscription(msg, true, (String)args.get(2), (IChannel)args.get(3)))
                                )
                                .onExecute((msg, args) -> this.manageSubscription(msg, true, (String)args.get(2), msg.getChannel()))
                            )
                        )
                        .optionArg(new String[] {"unsubscribe", "unsub"}, unsub -> unsub
                            .captureArg(name -> name
                                .channelArg(channel -> channel
                                    .onExecute((msg, args) -> this.manageSubscription(msg, false, (String)args.get(2), (IChannel)args.get(3)))
                                )
                                .onExecute((msg, args) -> this.manageSubscription(msg, false, (String)args.get(2), msg.getChannel()))
                            )
                        )
                        .stringArg("list", list -> list
                            .onExecute(this::listAnnouncements)
                        )
                )
                .buildCommand();
    }


    private void listAnnouncements(IMessage message, List<Object> args) {
        AnnouncerManager announcerManager = Bot.instance.getAnnouncerManager();
        String announcers = String.join(", ", announcerManager.getNames());
        message.getChannel().sendMessage(Localizer.localize("command.announce.list", announcers));
    }


    private void manageSubscription(IMessage message, boolean isSub, String announcerName, IChannel channel) {
        AnnouncerManager announcerManager = Bot.instance.getAnnouncerManager();

        IAnnouncer announcer = announcerManager.getAnnouncer(announcerName);
        if (announcer == null) {
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.no_such_announcement", announcerName));
            return;
        }

        BiConsumer<IAnnouncer, IChannel> method = isSub ? announcerManager::addSub : announcerManager::forgetSub;
        method.accept(announcer, channel);
        message.getChannel().sendMessage(Localizer.localize("command.announce.success." + (isSub ? "sub" : "unsub"), announcer.getName(), channel.toString()));
    }

    private void fail(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        message.getChannel().sendMessage(Localizer.localize("command.announce.error.no_subcommand", "subscribe, unsubscribe, list"));
    }
}
