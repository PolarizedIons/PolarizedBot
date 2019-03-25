package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Image;
import discord4j.core.object.util.Snowflake;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.CommandManager;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.config.GlobalConfig;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.BotInfo;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CommandAbout implements ICommand {
    private final Bot bot;

    public CommandAbout(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("About")
                .command("about", about -> about
                        .onExecute(this::about)
                        .setHelp("command.about.help.about")
                )
                .command("info", info -> info
                        .pingArg(user -> user
                            .onExecute((message, args) -> this.info(message, args.getAsUser(1)))
                        )
                        .onExecute((message, args) -> this.info(message, message.getAuthor().get()))
                        .setHelp("command.about.help.info")
                )
                .setHelp("command.about.help")
                .buildCommand();
    }

    private void about(Message message, ParsedArguments args) {
        Guild guild = message.getGuild().block();
        Localizer loc = new Localizer(guild);

        CommandManager commandManager = bot.getCommandManager();
        GlobalConfig globalConfig = bot.getGlobalConfig();
        GuildConfig guildConfig = this.bot.getGuildManager().getConfig(guild);
        User botOwner = bot.getClient().getApplicationInfo().block().getOwner().block();
        User botUser = bot.getClient().getSelf().block();

        Instant now = Instant.now();
        Duration runningTime = Duration.between(bot.getStartInstant(), now);
        Duration connectedTime = Duration.between(bot.getConnectedInstant(), now);

        int commandsNum = commandManager.getCommands().size();
        int commandsDisabled = new HashSet<>(guildConfig.disabledCommands.parallelStream().map(commandManager::get).collect(Collectors.toList())).size();

        int respondersNum = bot.getResponderManager().getResponders().size();
        int respondersDisabled = guildConfig.disabledResponders.size();

        int announcersNum = bot.getAnnouncerManager().getIDs().length;
        int announcersEnabled = bot.getAnnouncerManager().getAnnouncersForGuild(guild).size();

        String globalAdmins = globalConfig.globalAdmins.parallelStream().map(uId -> {
            User user = bot.getClient().getUserById(Snowflake.of(uId)).block();
            return user.getUsername() + "#" + user.getDiscriminator();
        }).collect(Collectors.joining(", "));

        message.getChannel().subscribe(channel ->
            channel.createMessage(msgSpec -> {
                msgSpec.setEmbed(embedSpec -> {
                    embedSpec.addField(loc.localize("command.about.header.bot_owner"), botOwner.getUsername() + "#" + botOwner.getDiscriminator(), false);
                    embedSpec.addField(loc.localize("command.about.header.global_admins"), globalAdmins, false);
                    embedSpec.addField(loc.localize("command.about.header.running"), TimeUtil.formatDuration(loc, runningTime), false);
                    embedSpec.addField(loc.localize("command.about.header.connected"), TimeUtil.formatDuration(loc, connectedTime), false);
                    embedSpec.addField(loc.localize("command.about.header.source_code"), BotInfo.githubRepo, false);

                    embedSpec.addField(loc.localize("command.about.header.bot_user"), botUser.getUsername() + "#" + botUser.getDiscriminator(), false);
                    embedSpec.addField(loc.localize("command.about.header.bot_id"), botUser.getId().asString(), false);

                    embedSpec.addField(loc.localize("command.about.header.commands"), loc.localize("command.about.info.commands", commandsNum, commandsDisabled), false);
                    embedSpec.addField(loc.localize("command.about.header.autoresponders"), loc.localize("command.about.info.autoresponders", respondersNum, respondersDisabled), false);
                    embedSpec.addField(loc.localize("command.about.header.announcers"), loc.localize("command.about.info.announcers", announcersNum, announcersEnabled), false);

                    embedSpec.setTitle(loc.localize("command.about.bot_info", botUser.asMember(guild.getId()).block().getDisplayName()));
                    embedSpec.setThumbnail(bot.getClient().getApplicationInfo().block().getIcon(Image.Format.PNG).get());
                    embedSpec.setFooter("PolarizedBot v" + BotInfo.version, null);

                    try {
                        embedSpec.setTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(BotInfo.buildtime).toInstant());
                    }
                    catch (ParseException e) {
                        /* NOOP */
                    }

                    embedSpec.setColor(new Color(57, 78, 160));

                });
            })
        );
    }

    private void info(@NotNull Message message, @NotNull User user) {
        Guild guild = message.getGuild().block();
        Localizer loc = new Localizer(guild);

        message.getChannel().subscribe(channel -> {
            channel.createMessage(msgSpec -> {
                msgSpec.setEmbed(embedSpec -> {
                    embedSpec.addField(loc.localize("command.about.header.user"), user.getUsername() + "#" + user.getDiscriminator(), true);
                    embedSpec.addField(loc.localize("command.about.header.user_id"), user.getId().asString(), true);
                    embedSpec.addField(loc.localize("command.about.header.rank"), loc.localize("ranks." + this.bot.getGuildManager().getUserRank(guild, user).name().toLowerCase()), true);

                    embedSpec.setTitle(loc.localize("command.about.user_info", user.asMember(guild.getId()).block().getDisplayName()));
                    embedSpec.setThumbnail(user.getAvatarUrl());
                    embedSpec.setFooter("PolarizedBot v" + BotInfo.version, null);
                    try {
                        embedSpec.setTimestamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(BotInfo.buildtime).toInstant());
                    }
                    catch (ParseException e) {
                        /* NOOP */
                    }

                    embedSpec.setColor(new Color(57, 78, 160));

                });
            });
        });

    }
}
