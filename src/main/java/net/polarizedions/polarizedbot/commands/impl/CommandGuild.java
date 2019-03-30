package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.autoresponders.ResponderManager;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandGuild implements ICommand {
    private static String[] subCommands = new String[] { "set", "enable", "disable", "mod", "unmod" };
    private static String[] setCommands = new String[] { "prefix", "lang", "rank" };
    private final Bot bot;

    public CommandGuild(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create(bot, "Guild")
                .setRank(UserRank.LOCAL_ADMIN)
                .command("guild", guild -> guild
                        .stringArg("set", set -> set
                                .stringArg("prefix", prefix -> prefix
                                        .captureArg(str -> str.onExecute(this::setPrefix))
                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "prefix_missing"))
                                )
                                .stringArg("lang", lang -> lang
                                        .captureArg(str -> str.onExecute(this::setLang))
                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "lang_missing"))
                                )
                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "unknown_set_command", (Object[])setCommands))
                        )
                        .stringArg("enable", enable -> enable
                                .stringArg("command", command -> command
                                        .captureArg(cmd -> cmd.onExecute(this::enableCommand))
                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_command_missing"))
                                )
                                .stringArg("responder", responder -> responder
                                        .captureArg(rsp -> rsp.onExecute(this::enableResponder))
                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_responder_missing", String.join(", ", this.bot.getGuildManager().getConfig(m.getGuild().block()).disabledResponders)))
                                )
                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_arg_missing", "command, responder"))
                        )
                        .stringArg("disable", disable -> disable
                                .stringArg("command", command -> command
                                        .captureArg(cmd -> cmd.onExecute(this::disableCommand))
                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_command_missing"))
                                )
                                .stringArg("responder", responder -> responder
                                        .captureArg(rsp -> rsp.onExecute(this::disableResponder))
                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_responder_missing", String.join(", ", this.bot.getResponderManager().getIDs())))
                                )
                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_arg_missing", "command, responder"))
                        )
                        .stringArg("mod", mod -> mod
                            .pingArg(user -> user
                                .onExecute(this::addModerator)
                            )
                            .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "mod_no_user"))
                        )
                        .stringArg("unmod", unmod -> unmod
                            .pingArg(user -> user
                                .onExecute(this::removeModerator)
                            )
                            .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "unmod_no_user"))
                        )
                        .onFail(this::subcommandFail)
                        .setHelp("command.guild.help.usage")
                )
                .setHelp("command.guild.help")
                .buildCommand();
    }

    private void subcommandFail(Message message, ParsedArguments parsedArgs, @NotNull List<String> unparsedArgs) {
        if (unparsedArgs.size() == 0) {
            MessageUtil.reply(message, "command.guild.error.no_subcommand", String.join(", ", subCommands));
        }
        else {
            MessageUtil.reply(message, "command.guild.error.unknown_subcommand", unparsedArgs.get(0), String.join(", ", subCommands));
        }
    }

    private void notEnoughArgs(Message message, ParsedArguments parsedArgs, List<String> unparsedArgs, String suffix, Object... context) {
        MessageUtil.reply(message, "command.guild.error." + suffix, context);
    }

    private void setLang(Message message, @NotNull ParsedArguments args) {
        String newLang = args.getAsString(2);

        if (!Localizer.supports(newLang)) {
            MessageUtil.reply(message, "command.guild.error.unknown_lang", newLang, String.join(", "), Localizer.AVAILABLE_LANGUAGES);
            return;
        }

        this.bot.getGuildManager().getConfig(message.getGuild().block()).lang = newLang.toLowerCase();
        this.bot.getGuildManager().saveConfig(message.getGuild().block());
        MessageUtil.reply(message, "command.guild.success.setlang", newLang);
    }

    private void addModerator(Message message, @NotNull ParsedArguments args) {
        User user = args.getAsUser(2);

        if (this.bot.getGuildManager().getUserRank(message.getGuild().block(), user) == UserRank.LOCAL_ADMIN) {
            MessageUtil.reply(message, "command.guild.error.already_local_admin", user.getMention());
            return;
        }

        this.bot.getGuildManager().setRank(message.getGuild().block(), user, UserRank.LOCAL_ADMIN);
        MessageUtil.reply(message, "command.guild.success.added_local_admin", user.getMention());
    }

    private void removeModerator(Message message, @NotNull ParsedArguments args) {
        User user = args.getAsUser(2);

        if (this.bot.getGuildManager().getUserRank(message.getGuild().block(), user) != UserRank.LOCAL_ADMIN) {
            MessageUtil.reply(message, "command.guild.error.not_local_admin", user.getMention());
            return;
        }

        this.bot.getGuildManager().setRank(message.getGuild().block(), user, UserRank.DEFAULT);
        MessageUtil.reply(message, "command.guild.success.removed_local_admin", user.getMention());
    }

    private void disableCommand(@NotNull Message message, @NotNull ParsedArguments args) {
        GuildConfig config = this.bot.getGuildManager().getConfig(message.getGuild().block());
        String commandName = args.getAsString(3);
        CommandTree command = this.bot.getCommandManager().get(commandName);

        if (command == null) {
            MessageUtil.reply(message, "command.guild.error.no_command_found", commandName);
        }
        else if (command.getName().equals(this.getCommand().getName())) {
            MessageUtil.reply(message, "command.guild.error.cannot_disable_self");
        }
        else if (config.disabledCommands.contains(commandName)) {
            MessageUtil.reply(message, "command.guild.error.already_disabled", command.getName());
        }
        else {
            config.disabledCommands.addAll(command.getCommands());
            this.bot.getGuildManager().saveConfig(message.getGuild().block());
            MessageUtil.reply(message, "command.guild.success.disable.command", command.getName());
        }
    }

    private void enableCommand(@NotNull Message message, @NotNull ParsedArguments args) {
        GuildConfig guildConfig = this.bot.getGuildManager().getConfig(message.getGuild().block());
        String commandName = args.getAsString(3);
        CommandTree command = this.bot.getCommandManager().get(commandName);

        if (command == null) {
            MessageUtil.reply(message, "command.guild.error.no_command_found", commandName);
            return;
        }

        if (!guildConfig.disabledCommands.contains(commandName)) {
            MessageUtil.reply(message, "command.guild.error.command_not_disabled", command.getName());
            return;
        }

        guildConfig.disabledCommands.removeAll(command.getCommands());
        this.bot.getGuildManager().saveConfig(message.getGuild().block());
        MessageUtil.reply(message, "command.guild.success.enable.command", command.getName());
    }

    private void disableResponder(@NotNull Message message, @NotNull ParsedArguments args) {
        ResponderManager manager = this.bot.getResponderManager();
        String toDisable = args.getAsString(3).toLowerCase();
        Guild guild = message.getGuild().block();

        if (!manager.getIDs().contains(toDisable)) {
            MessageUtil.reply(message, "command.guild.error.no_responder_found", toDisable, String.join(", ", manager.getIDs()));
            return;
        }
        else if (this.bot.getGuildManager().getConfig(guild).disabledResponders.contains(toDisable)) {
            MessageUtil.reply(message, "command.guild.error.responder_already_disabled", toDisable);
            return;
        }

        this.bot.getGuildManager().getConfig(guild).disabledResponders.add(toDisable);
        this.bot.getGuildManager().saveConfig(guild);
        MessageUtil.reply(message, "command.guild.success.disable.responder", toDisable);
    }

    private void enableResponder(@NotNull Message message, @NotNull ParsedArguments args) {
        ResponderManager manager = this.bot.getResponderManager();
        String toDisable = args.getAsString(3).toLowerCase();
        Guild guild = message.getGuild().block();

        if (!this.bot.getGuildManager().getConfig(guild).disabledResponders.contains(toDisable)) {
            MessageUtil.reply(message, "command.guild.error.responder_not_disabled", toDisable);
            return;
        }
        else if (!manager.getIDs().contains(toDisable)) {
            MessageUtil.reply(message, "command.guild.error.no_responder_found", toDisable, String.join(", ", manager.getIDs()));
            return;
        }

        this.bot.getGuildManager().getConfig(guild).disabledResponders.remove(toDisable);
        this.bot.getGuildManager().saveConfig(guild);
        MessageUtil.reply(message, "command.guild.success.enable.responder", toDisable);
    }

    private void setPrefix(@NotNull Message message, @NotNull ParsedArguments args) {
        this.bot.getGuildManager().getConfig(message.getGuild().block()).commandPrefix = args.getAsString(3);
        this.bot.getGuildManager().saveConfig(message.getGuild().block());
        MessageUtil.reply(message, "command.guild.success.set_prefix", args.getAsString(3));
    }
}
