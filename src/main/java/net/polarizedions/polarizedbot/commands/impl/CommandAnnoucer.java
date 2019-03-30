package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.announcer.AnnouncerManager;
import net.polarizedions.polarizedbot.announcer.IAnnouncer;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class CommandAnnoucer implements ICommand {
    private static String[] subcommands = new String[] { "subscribe", "unsubscribe", "list", "guild" };
    private final Bot bot;

    public CommandAnnoucer(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create(bot, "Announcer")
                .setRank(UserRank.LOCAL_ADMIN)
                .setHelp("announces things")
                .command("announce", announce -> announce
                        .optionArg(new String[] { "subscribe", "sub" }, sub -> sub
                                .captureArg(name -> name
                                        .channelArg(channel -> channel
                                                .onExecute((msg, args) -> this.manageSubscription(msg, true, args.getAsString(2), args.getAsChannel(3)))
                                                .onFail(this::failChannel)
                                        )
                                        .onExecute((msg, args) -> this.manageSubscription(msg, true, args.getAsString(2), (TextChannel)msg.getChannel().block()))
                                        .onFail(this::fail)
                                )
                        )
                        .optionArg(new String[] { "unsubscribe", "unsub" }, unsub -> unsub
                                .captureArg(name -> name
                                        .channelArg(channel -> channel
                                                .onExecute((msg, args) -> this.manageSubscription(msg, false, args.getAsString(2), args.getAsChannel(3)))
                                                .onFail(this::failChannel)
                                        )
                                        .onExecute((msg, args) -> this.manageSubscription(msg, false, args.getAsString(2), (TextChannel)msg.getChannel().block()))
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

    private void manageSubscription(Message message, boolean isSub, String announcerID, TextChannel channel) {
        AnnouncerManager announcerManager = this.bot.getAnnouncerManager();

        IAnnouncer announcer = announcerManager.getAnnouncer(announcerID);
        if (announcer == null) {
            MessageUtil.reply(message, "command.announce.error.no_such_announcement", announcerID);
            return;
        }

        BiConsumer<IAnnouncer, TextChannel> method = isSub ? announcerManager::addSub : announcerManager::forgetSub;
        method.accept(announcer, channel);
        Localizer loc = new Localizer(bot.getGuildManager().getConfig(message.getGuild().block()).lang);
        String name = loc.localize("announcer." + announcer.getID() + ".name");
        MessageUtil.reply(message, "command.announce.success." + ( isSub ? "sub" : "unsub" ), name, channel.toString());
    }

    private void listAnnouncements(Message message, ParsedArguments args) {
        AnnouncerManager announcerManager = this.bot.getAnnouncerManager();
        Localizer loc = new Localizer(bot.getGuildManager().getConfig(message.getGuild().block()).lang);
        String announcers = String.join(", ", Arrays.stream(announcerManager.getIDs()).map(id -> loc.localize("announcer." + id + ".name") + " (`" + id + "`)").collect(Collectors.toList()));
        MessageUtil.reply(message, "command.announce.list", announcers);
    }

    private void fail(Message message, @NotNull ParsedArguments parsedArgs, List<String> unparsedArgs) {
        if (parsedArgs.size() == 1) {
            MessageUtil.reply(message, "command.announce.error.no_subcommand", String.join(", ", subcommands));
        }
        else if (parsedArgs.size() == 2) {
            MessageUtil.reply(message, "command.announce.error.channel_or_announcer_missing");
        }
        else {
            MessageUtil.reply(message, "command.announce.error.announcer_missing");
        }
    }

    private void failChannel(Message message, ParsedArguments parsedArgs, List<String> unparsedArgs) {
        MessageUtil.reply(message, "command.announce.error.no_channel");
    }

    private void listGuild(@NotNull Message message, ParsedArguments args) {
        Map<IAnnouncer, List<TextChannel>> announcers = this.bot.getAnnouncerManager().getAnnouncersForGuild(message.getGuild().block());

        if (announcers.size() == 0) {
            MessageUtil.reply(message, "command.announce.error.no_announcements");
            return;
        }

        StringBuilder response = new StringBuilder("```\n");
        for (Map.Entry<IAnnouncer, List<TextChannel>> entry : announcers.entrySet()) {
            List<TextChannel> channels = entry.getValue();
            response.append(" * ").append(entry.getKey().getID()).append(": ").append("#").append(channels.get(0).getName()).append("\n");
            int len = 3 + entry.getKey().getID().length() + 2;
            for (int i = 1; i < channels.size(); i++) {
                for (int j = 0; j < len; j++) {
                    response.append(" ");
                }
                response.append("#").append(channels.get(i).getName()).append("\n");
            }
        }

        MessageUtil.sendAutosplit(message, response.append("```").toString(), "```", "```");
    }
}
