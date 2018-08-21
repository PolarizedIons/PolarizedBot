package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import io.github.polarizedions.polarizedbot.config.GuildConfig;
import io.github.polarizedions.polarizedbot.util.GuildManager;
import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.util.UserRank;
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
                                .captureArg(command -> command.onExecute(this::enableCommand))
                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_command_missing"))
                        )
                        .stringArg("disable", disable -> disable
                                .captureArg(command -> command.onExecute(this::disableCommand))
                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_command_missing"))
                        )
                        .onFail(this::subcommandFail)
                )
                .buildCommand();
    }

    private void subcommandFail(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        if (unparsedArgs.size() == 0) {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.no_subcommand", String.join(", ", subcommands)));
        }
        else {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.unknown_subcommand", unparsedArgs.get(0), String.join(", ", subcommands)));
        }
    }

    private void notEnoughArgs(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs, String suffix, Object... context) {
        message.getChannel().sendMessage(Localizer.localize("command.guild.error." + suffix, context));
    }

    private void setLang(IMessage message, List<Object> args) {

        String newLang = (String) args.get(2);
        if (!Localizer.supports(newLang)) {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.unknown_lang", newLang, String.join(", "), Localizer.AVAILABLE_LANGS));
            return;
        }

        GuildManager.getConfig(message.getGuild()).lang = newLang.toLowerCase();
        GuildManager.saveConfig(message.getGuild());
        message.getChannel().sendMessage(Localizer.localize("command.guild.success.setlang", newLang));
    }

    private void setRank(IMessage message, List<Object> args) {
        String rankName = (String) args.get(2);
        IUser user = (IUser) args.get(3);

        UserRank rank = UserRank.getByName(rankName);
        if (rank == null) {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.no_such_rank", rankName, String.join(", ", UserRank.getNames())));
            return;
        }

        GuildManager.setRank(message.getGuild(), user, rank);
        message.getChannel().sendMessage(Localizer.localize("command.guild.success.set_rank", user.toString(), rank.toString()));
    }

    private void disableCommand(IMessage message, List<Object> args) {
        GuildConfig config = GuildManager.getConfig(message.getGuild());
        String commandName = (String) args.get(2);
        CommandTree command = Bot.instance.getCommandManager().get(commandName);

        if (command == null) {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.no_command_found", commandName));
        }
        else if (command.getName().equals(this.getCommand().getName())) {
            System.out.println(command);
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.cannot_disable_self"));
        }
        else if (config.disabledCommands.contains(commandName)) {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.already_disabled", command.getName()));
        }
        else {
            config.disabledCommands.addAll(command.getCommands());
            GuildManager.saveConfig(message.getGuild());
            message.getChannel().sendMessage(Localizer.localize("command.guild.success.disable", command.getName()));
        }
    }

    private void enableCommand(IMessage message, List<Object> args) {
        GuildConfig guildConfig = GuildManager.getConfig(message.getGuild());
        String commandName = (String) args.get(2);
        CommandTree command = Bot.instance.getCommandManager().get(commandName);
        if (command == null) {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.no_command_found", commandName));
        }

        if (!guildConfig.disabledCommands.contains(commandName)) {
            message.getChannel().sendMessage(Localizer.localize("command.guild.error.command_not_disabled", command.getName()));
            return;
        }

        guildConfig.disabledCommands.removeAll(command.getCommands());
        GuildManager.saveConfig(message.getGuild());
        message.getChannel().sendMessage(Localizer.localize("command.guild.success.enable", command.getName()));
    }

    private void setPrefix(IMessage message, List<Object> args) {
        GuildManager.getConfig(message.getGuild()).commandPrefix = (String) args.get(3);
        GuildManager.saveConfig(message.getGuild());
        message.getChannel().sendMessage(Localizer.localize("command.guild.success.set_prefix", args.get(3)));
    }
}
