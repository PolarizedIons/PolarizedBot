package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.announcer.IAnnouncer;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.UserRank;
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
                                        .onFail(this::failChannel)
                                )
                                .onExecute((msg, args) -> this.manageSubscription(msg, true, (String)args.get(2), msg.getChannel()))
                                .onFail(this::fail)
                            )
                        )
                        .optionArg(new String[] {"unsubscribe", "unsub"}, unsub -> unsub
                            .captureArg(name -> name
                                .channelArg(channel -> channel
                                    .onExecute((msg, args) -> this.manageSubscription(msg, false, (String)args.get(2), (IChannel)args.get(3)))
                                    .onFail(this::failChannel)
                                )
                                .onExecute((msg, args) -> this.manageSubscription(msg, false, (String)args.get(2), msg.getChannel()))
                                .onFail(this::fail)
                            )
                        )
                        .stringArg("list", list -> list
                            .onExecute(this::listAnnouncements)
                        )
                        .onFail(this::fail)
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
        if (parsedArgs.size() == 1) {
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.no_subcommand", "subscribe, unsubscribe, list"));
        }
        else if (parsedArgs.size() == 2){
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.channel_or_announcer_missing"));
        }
        else {
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.announcer_missing"));
        }
    }

    private void failChannel(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        message.getChannel().sendMessage(Localizer.localize("command.announce.error.no_channel"));
    }
}
