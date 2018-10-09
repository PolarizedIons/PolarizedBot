package net.polarizedions.polarizedbot.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.commands.CommandSource;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.polarizedions.polarizedbot.commands.BrigadierTypeFixHelper.argument;
import static net.polarizedions.polarizedbot.commands.BrigadierTypeFixHelper.literal;
import static net.polarizedions.polarizedbot.commands.arguments.UserArgumentType.getUser;
import static net.polarizedions.polarizedbot.commands.arguments.UserArgumentType.user;
import static net.polarizedions.polarizedbot.commands.arguments.UserRankArgumentType.getRankUser;
import static net.polarizedions.polarizedbot.commands.arguments.UserRankArgumentType.userRank;

//import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
//import net.polarizedions.polarizedbot.commands.builder.CommandTree;
//import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;

public class CommandGuild implements ICommand {
    @Override
    public void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("guild")
                .requires(source -> source.hasPermission(UserRank.GUILD_ADMIN))
                .then(
                        literal("set")
                                .then(
                                        literal("prefix")
                                                .then(
                                                        argument("value", word())
                                                                .executes(c -> this.setPrefix(c.getSource().guild, getString(c, "value")))
                                                )
                                )
                                .then(
                                        literal("lang")
                                                .then(
                                                        argument("value", word())
                                                                .executes(c -> this.setLang(c.getSource().guild, getString(c, "value")))
                                                )
                                )
                                .then(
                                        literal("rank")
                                                .then(
                                                        argument("user", user())
                                                                .then(
                                                                        argument("rank", userRank())
                                                                                .executes(c -> this.setRank(c.getSource().guild, getUser(c, "user"), getRankUser(c, "rank")))
                                                                )
                                                )
                                )
                )
                .then(
                        literal("enable")
                                .then(
                                        literal("command")
                                                .then(
                                                        argument("value", word())
                                                                .executes(c -> this.enableCommand(c.getSource().guild, getString(c, "value")))
                                                )
                                )
                                .then(
                                        literal("responder")
                                                .then(
                                                        argument("value", word())
                                                                .executes(c -> this.enableResponder(c.getSource().guild, getString(c, "value")))
                                                )
                                )
                )
                .then(
                        literal("disable")
                                .then(
                                        literal("command")
                                                .then(
                                                        argument("value", word())
                                                                .executes(c -> this.disableCommand(c.getSource().guild, getString(c, "value")))
                                                )
                                )
                                .then(
                                        literal("responder")
                                                .then(
                                                        argument("value", word())
                                                                .executes(c -> this.disableResponder(c.getSource().guild, getString(c, "value")))
                                                )
                                )
                )
        );
    }

    private int disableResponder(IGuild guild, String value) {
        return 0;
    }

    private int disableCommand(IGuild guild, String value) {
        return 0;
    }

    private int enableResponder(IGuild guild, String value) {
        return 0;
    }

    private int enableCommand(IGuild guild, String value) {
        return 0;
    }

    private int setRank(IGuild guild, IUser user, UserRank rank) {
        return 0;
    }

    private int setLang(IGuild guild, String value) {
        return 0;
    }

    private int setPrefix(IGuild guild, String value) {
        System.out.println(guild + value);
        return 0;
    }

    @Override
    public void help(CommandDispatcher<CommandSource> dispatcher, CommandSource source) {

    }
//    private static String[] subcommands = new String[] { "set", "enable", "disable" };
//    private static String[] setCommands = new String[] { "prefix", "lang", "rank" };
//
//    @Override
//    public CommandTree getCommand() {
//        return CommandBuilder.create("Guild")
//                .setRank(UserRank.GUILD_ADMIN)
//                .command("guild", guild -> guild
//                        .stringArg("set", set -> set
//                                .stringArg("prefix", prefix -> prefix
//                                        .captureArg(str -> str.onExecute(this::setPrefix))
//                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "prefix_missing"))
//                                )
//                                .stringArg("lang", lang -> lang
//                                        .captureArg(str -> str.onExecute(this::setLang))
//                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "lang_missing"))
//                                )
//                                .stringArg("rank", rank -> rank
//                                        .captureArg(rankStr -> rankStr
//                                                .pingArg(user -> user.onExecute(this::setRank))
//                                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "rank_user_missing"))
//                                        )
//                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "rank_missing"))
//                                )
//                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "unknown_set_command", (Object[])setCommands))
//                        )
//                        .stringArg("enable", enable -> enable
//                                .stringArg("command", command -> command
//                                        .captureArg(cmd -> cmd.onExecute(this::enableCommand))
//                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_command_missing"))
//                                )
//                                .stringArg("responder", responder -> responder
//                                        .captureArg(rsp -> rsp.onExecute(this::enableResponder))
//                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_responder_missing", String.join(", ", GuildManager.getConfig(m.getGuild()).disabledResponders)))
//                                )
//                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "enable_arg_missing", "command, responder"))
//                        )
//                        .stringArg("disable", disable -> disable
//                                .stringArg("command", command -> command
//                                        .captureArg(cmd -> cmd.onExecute(this::disableCommand))
//                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_command_missing"))
//                                )
//                                .stringArg("responder", responder -> responder
//                                        .captureArg(rsp -> rsp.onExecute(this::disableResponder))
//                                        .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_responder_missing", String.join(", ", Bot.instance.getResponderManager().getIDs())))
//                                )
//                                .onFail((m, p, u) -> this.notEnoughArgs(m, p, u, "disable_arg_missing", "command, responder"))
//                        )
//                        .onFail(this::subcommandFail)
//                        .setHelp("command.guild.help.usage")
//                )
//                .setHelp("command.guild.help")
//                .buildCommand();
//    }
//
//    private void subcommandFail(IMessage message, ParsedArguments parsedArgs, @NotNull List<String> unparsedArgs) {
//        if (unparsedArgs.size() == 0) {
//            MessageUtil.reply(message, "command.guild.error.no_subcommand", String.join(", ", subcommands));
//        }
//        else {
//            MessageUtil.reply(message, "command.guild.error.unknown_subcommand", unparsedArgs.get(0), String.join(", ", subcommands));
//        }
//    }
//
//    private void notEnoughArgs(IMessage message, ParsedArguments parsedArgs, List<String> unparsedArgs, String suffix, Object... context) {
//        MessageUtil.reply(message, "command.guild.error." + suffix, context);
//    }
//
//    private void setLang(IMessage message, @NotNull ParsedArguments args) {
//        String newLang = args.getAsString(2);
//
//        if (!Localizer.supports(newLang)) {
//            MessageUtil.reply(message, "command.guild.error.unknown_lang", newLang, String.join(", "), Localizer.AVAILABLE_LANGUAGES);
//            return;
//        }
//
//        GuildManager.getConfig(message.getGuild()).lang = newLang.toLowerCase();
//        GuildManager.saveConfig(message.getGuild());
//        MessageUtil.reply(message, "command.guild.success.setlang", newLang);
//    }
//
//    private void setRank(IMessage message, @NotNull ParsedArguments args) {
//        String rankName = args.getAsString(3);
//        IUser user = args.getAsUser(4);
//
//        UserRank rank = UserRank.getByName(rankName);
//        if (rank == null) {
//            MessageUtil.reply(message, "command.guild.error.no_such_rank", rankName, String.join(", ", UserRank.getNames()));
//            return;
//        }
//
//        GuildManager.setRank(message.getGuild(), user, rank);
//        MessageUtil.reply(message, "command.guild.success.set_rank", user.toString(), rank.toString());
//    }
//
//    private void disableCommand(@NotNull IMessage message, @NotNull ParsedArguments args) {
//        GuildConfig config = GuildManager.getConfig(message.getGuild());
//        String commandName = args.getAsString(3);
//        CommandTree command = Bot.instance.getCommandManager().get(commandName);
//
//        if (command == null) {
//            MessageUtil.reply(message, "command.guild.error.no_command_found", commandName);
//        }
//        else if (command.getName().equals(this.getCommand().getName())) {
//            MessageUtil.reply(message, "command.guild.error.cannot_disable_self");
//        }
//        else if (config.disabledCommands.contains(commandName)) {
//            MessageUtil.reply(message, "command.guild.error.already_disabled", command.getName());
//        }
//        else {
//            config.disabledCommands.addAll(command.getCommands());
//            GuildManager.saveConfig(message.getGuild());
//            MessageUtil.reply(message, "command.guild.success.disable.command", command.getName());
//        }
//    }
//
//    private void enableCommand(@NotNull IMessage message, @NotNull ParsedArguments args) {
//        GuildConfig guildConfig = GuildManager.getConfig(message.getGuild());
//        String commandName = args.getAsString(3);
//        CommandTree command = Bot.instance.getCommandManager().get(commandName);
//
//        if (command == null) {
//            MessageUtil.reply(message, "command.guild.error.no_command_found", commandName);
//            return;
//        }
//
//        if (!guildConfig.disabledCommands.contains(commandName)) {
//            MessageUtil.reply(message, "command.guild.error.command_not_disabled", command.getName());
//            return;
//        }
//
//        guildConfig.disabledCommands.removeAll(command.getCommands());
//        GuildManager.saveConfig(message.getGuild());
//        MessageUtil.reply(message, "command.guild.success.enable.command", command.getName());
//    }
//
//    private void disableResponder(@NotNull IMessage message, @NotNull ParsedArguments args) {
//        ResponderManager manager = Bot.instance.getResponderManager();
//        String toDisable = args.getAsString(3).toLowerCase();
//        IGuild guild = message.getGuild();
//
//        if (!manager.getIDs().contains(toDisable)) {
//            MessageUtil.reply(message, "command.guild.error.no_responder_found", toDisable, String.join(", ", manager.getIDs()));
//            return;
//        }
//        else if (GuildManager.getConfig(guild).disabledResponders.contains(toDisable)) {
//            MessageUtil.reply(message, "command.guild.error.responder_already_disabled", toDisable);
//            return;
//        }
//
//        GuildManager.getConfig(guild).disabledResponders.add(toDisable);
//        GuildManager.saveConfig(guild);
//        MessageUtil.reply(message, "command.guild.success.disable.responder", toDisable);
//    }
//
//    private void enableResponder(@NotNull IMessage message, @NotNull ParsedArguments args) {
//        ResponderManager manager = Bot.instance.getResponderManager();
//        String toDisable = args.getAsString(3).toLowerCase();
//        IGuild guild = message.getGuild();
//
//        if (!GuildManager.getConfig(guild).disabledResponders.contains(toDisable)) {
//            MessageUtil.reply(message, "command.guild.error.responder_not_disabled", toDisable);
//            return;
//        }
//        else if (!manager.getIDs().contains(toDisable)) {
//            MessageUtil.reply(message, "command.guild.error.no_responder_found", toDisable, String.join(", ", manager.getIDs()));
//            return;
//        }
//
//        GuildManager.getConfig(guild).disabledResponders.remove(toDisable);
//        GuildManager.saveConfig(guild);
//        MessageUtil.reply(message, "command.guild.success.enable.responder", toDisable);
//    }
//
//    private void setPrefix(@NotNull IMessage message, @NotNull ParsedArguments args) {
//        GuildManager.getConfig(message.getGuild()).commandPrefix = args.getAsString(3);
//        GuildManager.saveConfig(message.getGuild());
//        MessageUtil.reply(message, "command.guild.success.set_prefix", args.getAsString(3));
//    }
}
