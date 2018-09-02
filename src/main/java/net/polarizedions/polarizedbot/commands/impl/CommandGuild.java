package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.autoresponders.ResponderManager;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.config.GuildConfig;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class CommandGuild implements ICommand {
    private static String[] subcommands = new String[] {"set", "enable", "disable"};
    private static String[] setCommands = new String[] {"prefix", "lang", "rank"};

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Guild")
                .setRank(UserRank.GUILD_ADMIN)
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
                                .stringArg("rank", rank -> rank
                                        .captureArg(rankStr -> rankStr
                                                .pingArg(user -> user.onExecute(this::setRank))
                                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "rank_user_missing"))
                                        )
                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "rank_missing"))
                                )
                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "unknown_set_command", (Object[]) setCommands))
                        )
                        .stringArg("enable", enable -> enable
                                .stringArg("command", command -> command
                                    .captureArg(cmd -> cmd.onExecute(this::enableCommand))
                                    .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_command_missing"))
                                )
                                .stringArg("responder", responder -> responder
                                    .captureArg(rsp -> rsp.onExecute(this::enableResponder))
                                    .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_responder_missing", String.join(", ", GuildManager.getConfig(m.getGuild()).disabledResponders)))
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
                                    .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_responder_missing", String.join(", ", Bot.instance.getResponderManager().getIDs())))
                                )
                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_arg_missing", "command, responder"))
                        )
                        .onFail(this::subcommandFail)
                        .setHelp("command.guild.help.usage")
                )
                 .setHelp("command.guild.help")
                .buildCommand();
    }

    private void subcommandFail(IMessage message, List<Object> parsedArgs, @NotNull List<String> unparsedArgs) {
        if (unparsedArgs.size() == 0) {
            MessageUtil.reply(message,"command.guild.error.no_subcommand", String.join(", ", subcommands));
        }
        else {
            MessageUtil.reply(message,"command.guild.error.unknown_subcommand", unparsedArgs.get(0), String.join(", ", subcommands));
        }
    }

    private void notEnoughArgs(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs, String suffix, Object... context) {
        MessageUtil.reply(message,"command.guild.error." + suffix, context);
    }

    private void setLang(IMessage message, @NotNull List<Object> args) {
        String newLang = (String) args.get(2);

        if (!Localizer.supports(newLang)) {
            MessageUtil.reply(message,"command.guild.error.unknown_lang", newLang, String.join(", "), Localizer.AVAILABLE_LANGUAGES);
            return;
        }

        GuildManager.getConfig(message.getGuild()).lang = newLang.toLowerCase();
        GuildManager.saveConfig(message.getGuild());
        MessageUtil.reply(message,"command.guild.success.setlang", newLang);
    }

    private void setRank(IMessage message, @NotNull List<Object> args) {
        String rankName = (String) args.get(3);
        IUser user = (IUser) args.get(4);

        UserRank rank = UserRank.getByName(rankName);
        if (rank == null) {
            MessageUtil.reply(message,"command.guild.error.no_such_rank", rankName, String.join(", ", UserRank.getNames()));
            return;
        }

        GuildManager.setRank(message.getGuild(), user, rank);
        MessageUtil.reply(message,"command.guild.success.set_rank", user.toString(), rank.toString());
    }

    private void disableCommand(@NotNull IMessage message, @NotNull List<Object> args) {
        GuildConfig config = GuildManager.getConfig(message.getGuild());
        String commandName = (String) args.get(3);
        CommandTree command = Bot.instance.getCommandManager().get(commandName);

        if (command == null) {
            MessageUtil.reply(message,"command.guild.error.no_command_found", commandName);
        }
        else if (command.getName().equals(this.getCommand().getName())) {
            MessageUtil.reply(message,"command.guild.error.cannot_disable_self");
        }
        else if (config.disabledCommands.contains(commandName)) {
            MessageUtil.reply(message,"command.guild.error.already_disabled", command.getName());
        }
        else {
            config.disabledCommands.addAll(command.getCommands());
            GuildManager.saveConfig(message.getGuild());
            MessageUtil.reply(message,"command.guild.success.disable.command", command.getName());
        }
    }

    private void enableCommand(@NotNull IMessage message, @NotNull List<Object> args) {
        GuildConfig guildConfig = GuildManager.getConfig(message.getGuild());
        String commandName = (String) args.get(3);
        CommandTree command = Bot.instance.getCommandManager().get(commandName);

        if (command == null) {
            MessageUtil.reply(message,"command.guild.error.no_command_found", commandName);
            return;
        }

        if (!guildConfig.disabledCommands.contains(commandName)) {
            MessageUtil.reply(message,"command.guild.error.command_not_disabled", command.getName());
            return;
        }

        guildConfig.disabledCommands.removeAll(command.getCommands());
        GuildManager.saveConfig(message.getGuild());
        MessageUtil.reply(message,"command.guild.success.enable.command", command.getName());
    }

    private void disableResponder(@NotNull IMessage message, @NotNull List<Object> args) {
        ResponderManager manager = Bot.instance.getResponderManager();
        String toDisable = ((String) args.get(3)).toLowerCase();
        IGuild guild = message.getGuild();

        if (! manager.getIDs().contains(toDisable)) {
            MessageUtil.reply(message,"command.guild.error.no_responder_found", toDisable, String.join(", ", manager.getIDs()));
            return;
        }
        else if (GuildManager.getConfig(guild).disabledResponders.contains(toDisable)) {
            MessageUtil.reply(message,"command.guild.error.responder_already_disabled", toDisable);
            return;
        }

        GuildManager.getConfig(guild).disabledResponders.add(toDisable);
        GuildManager.saveConfig(guild);
        MessageUtil.reply(message,"command.guild.success.disable.responder", toDisable);
    }

    private void enableResponder(@NotNull IMessage message, @NotNull List<Object> args) {
        ResponderManager manager = Bot.instance.getResponderManager();
        String toDisable = ((String) args.get(3)).toLowerCase();
        IGuild guild = message.getGuild();

        if (!GuildManager.getConfig(guild).disabledResponders.contains(toDisable)) {
            MessageUtil.reply(message,"command.guild.error.responder_not_disabled", toDisable);
            return;
        }
        else if (!manager.getIDs().contains(toDisable)) {
            MessageUtil.reply(message,"command.guild.error.no_responder_found", toDisable, String.join(", ", manager.getIDs()));
            return;
        }

        GuildManager.getConfig(guild).disabledResponders.remove(toDisable);
        GuildManager.saveConfig(guild);
        MessageUtil.reply(message,"command.guild.success.enable.responder", toDisable);
    }

    private void setPrefix(@NotNull IMessage message, @NotNull List<Object> args) {
        GuildManager.getConfig(message.getGuild()).commandPrefix = (String) args.get(3);
        GuildManager.saveConfig(message.getGuild());
        MessageUtil.reply(message,"command.guild.success.set_prefix", args.get(3));
    }
}
