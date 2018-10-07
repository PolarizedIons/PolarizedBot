package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.CommandManager;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.BuildInfo;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.TimeUtil;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CommandAbout implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("About")
                .command("about", about -> about
                        .onExecute(this::about)
                        .setHelp("command.about.help.about")
                )
                .command("info", info -> info
                        .onExecute(this::info)
                        .setHelp("command.about.help.info")
                )
                .setHelp("command.about.help")
                .buildCommand();
    }

    private void about(IMessage message, ParsedArguments args) {
        Localizer loc = new Localizer(message);

        Bot bot = Bot.instance;
        CommandManager commandManager = bot.getCommandManager();
        IGuild guild = message.getGuild();
        GlobalConfig globalConfig = bot.getGlobalConfig();
        GuildConfig guildConfig = GuildManager.getConfig(guild);

        EmbedBuilder builder = new EmbedBuilder();

        IUser owner = bot.getClient().getUserByID(Long.parseLong(globalConfig.owner));
        IUser botUser = bot.getClient().getOurUser();

        Instant now = Instant.now();
        Duration runningTime = Duration.between(bot.getStartInstant(), now);
        Duration connectedTime = Duration.between(bot.getConnectedInstant(), now);

        int commandsNum = commandManager.getCommands().size();
        int commandsDisabled = new HashSet<>(guildConfig.disabledCommands.parallelStream().map(commandManager::get).collect(Collectors.toList())).size();

        int respondersNum = bot.getResponderManager().getResponders().size();
        int respondersDisabled = guildConfig.disabledResponders.size();

        int announcersNum = bot.getAnnouncerManager().getIDs().length;
        int announcersEnabled = bot.getAnnouncerManager().getAnnouncersForGuild(guild).size();

        builder.appendField(loc.localize("command.about.header.owner"), owner.getName() + "#" + owner.getDiscriminator(), false);
        builder.appendField(loc.localize("command.about.header.running"), TimeUtil.formatDuration(loc, runningTime), false);
        builder.appendField(loc.localize("command.about.header.connected"), TimeUtil.formatDuration(loc, connectedTime), false);
        builder.appendField(loc.localize("command.about.header.source_code"), BuildInfo.githubRepo, false);

        builder.appendField(loc.localize("command.about.header.bot_user"), botUser.getName() + "#" + botUser.getDiscriminator(), false);
        builder.appendField(loc.localize("command.about.header.bot_id"), botUser.getStringID(), false);

        builder.appendField(loc.localize("command.about.header.commands"), loc.localize("command.about.info.commands", commandsNum, commandsDisabled), false);
        builder.appendField(loc.localize("command.about.header.autoresponders"), loc.localize("command.about.info.autoresponders", respondersNum, respondersDisabled), false);
        builder.appendField(loc.localize("command.about.header.announcers"), loc.localize("command.about.info.announcers", announcersNum, announcersEnabled), false);

        builder.withTitle(loc.localize("command.about.bot_info", bot.getClient().getOurUser().getDisplayName(guild)));
        builder.withThumbnail(bot.getClient().getApplicationIconURL());
        builder.withFooterText("PolarizedBot v" + BuildInfo.version + ", built: " + BuildInfo.buildtime);

        message.getChannel().sendMessage(builder.build());
    }

    private void info(IMessage message, ParsedArguments args) {
        Localizer loc = new Localizer(message);
        EmbedBuilder builder = new EmbedBuilder();
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();

        builder.appendField(loc.localize("command.about.header.user"), user.getName() + "#" + user.getDiscriminator(), true);
        builder.appendField(loc.localize("command.about.header.user_id"), user.getStringID(), true);
        builder.appendField(loc.localize("command.about.header.rank"), GuildManager.getUserRank(guild, user).name(), true);

        builder.withTitle(loc.localize("command.about.user_info", user.getDisplayName(guild)));
        builder.withThumbnail(message.getAuthor().getAvatarURL());
        builder.withFooterText("PolarizedBot v" + BuildInfo.version + ", built: " + BuildInfo.buildtime);

        message.getChannel().sendMessage(builder.build());
    }
}
