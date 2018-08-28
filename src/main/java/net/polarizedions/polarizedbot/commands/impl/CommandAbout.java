package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.announcer.IAnnouncer;
import net.polarizedions.polarizedbot.commands.CommandManager;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.TimeUtil;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandAbout implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("About")
                .command("about", about -> about
                        .onExecute(this::about)
                )
                .command("info", info -> info
                    .onExecute(this::info)
                )
                .buildCommand();
    }

    private void about(IMessage message, List<Object> args) {
        EmbedBuilder builder = new EmbedBuilder();
        Bot bot = Bot.instance;
        GlobalConfig globalConfig = bot.getGlobalConfig();
        IGuild guild = message.getGuild();
        GuildConfig guildConfig = GuildManager.getConfig(guild);
        CommandManager commandManager = bot.getCommandManager();
        IUser owner = bot.getClient().getUserByID(Long.parseLong(globalConfig.owner));
        IUser botUser = bot.getClient().getOurUser();

        Instant now = Instant.now();
        Duration runningTime = Duration.between(bot.getStartInstant(), now);
        Duration connectedTime = Duration.between(bot.getConnectedInstant(), now);

        int commandsNum = commandManager.getCommands().size();
        int commandsDisabled = new HashSet<>(guildConfig.disabledCommands.parallelStream().map(commandManager::get).collect(Collectors.toList())).size();

        int respondersNum = bot.getResponderManager().getResponders().size();
        int respondersDisabled = guildConfig.disabledResponders.size();

        int announcersNum = bot.getAnnouncerManager().getNames().length;
        int announcersEnabled = bot.getAnnouncerManager().getAnnouncersForGuild(guild).size();

        builder.appendField(Localizer.localize("command.about.header.owner"), owner.getName() + "#" + owner.getDiscriminator(), false);
        builder.appendField(Localizer.localize("command.about.header.running"), TimeUtil.formatDuration(runningTime), false);
        builder.appendField(Localizer.localize("command.about.header.connected"), TimeUtil.formatDuration(connectedTime), false);

        builder.appendField(Localizer.localize("command.about.header.bot_user"), botUser.getName() + "#" + botUser.getDiscriminator(), false);
        builder.appendField(Localizer.localize("command.about.header.bot_id"), botUser.getStringID(), false);

        builder.appendField(Localizer.localize("command.about.header.commands"), Localizer.localize("command.about.info.commands", commandsNum, commandsDisabled), false);
        builder.appendField(Localizer.localize("command.about.header.autoresponders"), Localizer.localize("command.about.info.autoresponders", respondersNum, respondersDisabled), false);
        builder.appendField(Localizer.localize("command.about.header.announcers"), Localizer.localize("command.about.info.announcers", announcersNum, announcersEnabled), false);

        builder.withTitle(Localizer.localize("command.about.bot_info", bot.getClient().getOurUser().getDisplayName(guild)));
        builder.withThumbnail(bot.getClient().getApplicationIconURL());
        builder.withFooterText("PolarizedBot v" + Bot.VERSION);

        message.getChannel().sendMessage(builder.build());
    }

    private void info(IMessage message, List<Object> args) {
        EmbedBuilder builder = new EmbedBuilder();
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();

        builder.appendField(Localizer.localize("command.about.header.user"), user.getName() + "#" + user.getDiscriminator(), true);
        builder.appendField(Localizer.localize("command.about.header.user_id"), user.getStringID(), true);
        builder.appendField(Localizer.localize("command.about.header.rank"), GuildManager.getUserRank(guild, user).name(), true);

        builder.withTitle(Localizer.localize("command.about.user_info", user.getDisplayName(guild)));
        builder.withThumbnail(message.getAuthor().getAvatarURL());
        builder.withFooterText("PolarizedBot v" + Bot.VERSION);

        message.getChannel().sendMessage(builder.build());
    }
}
