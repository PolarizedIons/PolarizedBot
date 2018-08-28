package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.announcer.IAnnouncer;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CommandAnnoucer implements ICommand {
    private static String[] subcommands = new String[] {"subscribe", "unsubscribe", "list", "guild"};
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Announcer")
                 .setRank(UserRank.GUILD_ADMIN)
                 .setHelp("announces things")
                 .command("announce", announce -> announce
                         .optionArg(new String[]{"subscribe", "sub"}, sub -> sub
                                 .captureArg(name -> name
                                         .channelArg(channel -> channel
                                                 .onExecute((msg, args) -> this.manageSubscription(msg, true, (String) args.get(2), (IChannel) args.get(3)))
                                                 .onFail(this::failChannel)
                                         )
                                         .onExecute((msg, args) -> this.manageSubscription(msg, true, (String) args.get(2), msg.getChannel()))
                                         .onFail(this::fail)
                                 )
                         )
                         .optionArg(new String[]{"unsubscribe", "unsub"}, unsub -> unsub
                                 .captureArg(name -> name
                                         .channelArg(channel -> channel
                                                 .onExecute((msg, args) -> this.manageSubscription(msg, false, (String) args.get(2), (IChannel) args.get(3)))
                                                 .onFail(this::failChannel)
                                         )
                                         .onExecute((msg, args) -> this.manageSubscription(msg, false, (String) args.get(2), msg.getChannel()))
                                         .onFail(this::fail)
                                 )
                         )
                         .stringArg("list", list -> list
                                 .onExecute(this::listAnnouncements)
                         )
                         .stringArg("guild", guild -> guild
                                 .onExecute(this::listGuild)
                         )
                         .onFail(this::fail)
                         .setHelp("command.announce.help.usage")
                 )
                 .setHelp("command.announce.help")
                 .buildCommand();
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

    private void listAnnouncements(IMessage message, List<Object> args) {
        AnnouncerManager announcerManager = Bot.instance.getAnnouncerManager();
        String announcers = String.join(", ", announcerManager.getNames());
        message.getChannel().sendMessage(Localizer.localize("command.announce.list", announcers));
    }

    private void fail(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        if (parsedArgs.size() == 1) {
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.no_subcommand", String.join(", ", subcommands)));
        } else if (parsedArgs.size() == 2) {
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.channel_or_announcer_missing"));
        } else {
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.announcer_missing"));
        }
    }

    private void failChannel(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        message.getChannel().sendMessage(Localizer.localize("command.announce.error.no_channel"));
    }

    private void listGuild(IMessage message, List<Object> args) {
        Map<IAnnouncer, List<IChannel>> announcers = Bot.instance.getAnnouncerManager().getAnnouncersForGuild(message.getGuild());

        if (announcers.size() == 0) {
            message.getChannel().sendMessage(Localizer.localize("command.announce.error.no_announcements"));
            return;
        }

        StringBuilder response = new StringBuilder("```\n");
        for (Map.Entry<IAnnouncer, List<IChannel>> entry : announcers.entrySet()) {
            List<IChannel> channels = entry.getValue();
            response.append(" * ").append(entry.getKey().getName()).append(": ").append("#").append(channels.get(0).getName()).append("\n");
            int len = 3 + entry.getKey().getName().length() + 2;
            for (int i = 1; i < channels.size(); i++) {
                for (int j = 0; j < len; j++) {
                    response.append(" ");
                }
                response.append("#").append(channels.get(i).getName()).append("\n");
            }
        }

        MessageUtil.sendAutosplit(message.getChannel(), response.append("```").toString());
    }
}
