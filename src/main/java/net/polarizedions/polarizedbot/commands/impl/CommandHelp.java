package net.polarizedions.polarizedbot.commands.impl;


import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import sx.blah.discord.handle.obj.IMessage;

import java.util.List;
import java.util.Set;

public class CommandHelp implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Help")
                .command("help", help -> help
                        .captureArg(cmd -> cmd
                                .onExecute(this::cmd)
                        )
                        .onExecute(this::list)
                        .setHelp("command.help.help.usage")
                )
                .setHelp("command.help.help")
                .buildCommand();
    }

    private void list(IMessage message, List<Object> args) {
        Set<CommandTree> commandSet = Bot.instance.getCommandManager().getCommands();
        int commandLen = commandSet.parallelStream().map(tree -> tree.getName().length()).max(Integer::compareTo).orElse(10);

        StringBuilder resp = new StringBuilder("```\nCommands:\n=====================\n");

        for (CommandTree cmd : commandSet) {
            resp.append("  * ").append(String.format("%-" + commandLen + "s", cmd.getName())).append("   - ").append(String.join(" | ", cmd.getCommands())).append("\n");
        }

        MessageUtil.sendAutosplit(message.getChannel(), resp.append("```").toString());
    }

    private void cmd(IMessage message, List<Object> args) {
        String helpCommand = (String)args.get(1);
        CommandTree command = null;
        outer:
        for (CommandTree cmd : Bot.instance.getCommandManager().getCommands()) {
            for (String alias : cmd.getCommands()) {
                if (alias.equalsIgnoreCase(helpCommand)) {
                    command = cmd;
                    break outer;
                }
            }
        }

        if (command == null) {
            MessageUtil.reply(message, "command.help.error.not_found", helpCommand);
            return;
        }

        Localizer loc = new Localizer(message);
        StringBuilder resp = new StringBuilder("```\n")
                .append(command.getName())
                .append("\n  - ");
        resp.append(loc.localize(( loc.doesKeyExist(command.getHelp()) ? command.getHelp() : "command.help.error.no_command_help" ), command.getName()))
                .append("\n");

        Set<String> aliasSet = command.getCommands();
        int aliasLen = aliasSet.parallelStream().map(String::length).max(Integer::compareTo).orElse(10);
        if (command.getCommands().size() > 0) {
            resp.append("=====================\n");
        }
        for (String alias : aliasSet) {
            boolean helpAlias = alias.equalsIgnoreCase(helpCommand);
            resp.append("  ")
                    .append(helpAlias ? "*" : " ")
                    .append(String.format("%-" + ( helpAlias ? aliasLen - 1 : aliasLen ) + "s", alias)).append(" - ")
                    .append(loc.localize(loc.doesKeyExist(command.getHelpFor(alias)) ? command.getHelpFor(alias) : "command.help.error.no_alias_help", alias))
                    .append("\n");
        }

        MessageUtil.sendAutosplit(message.getChannel(), resp.append("```").toString());
    }
}

