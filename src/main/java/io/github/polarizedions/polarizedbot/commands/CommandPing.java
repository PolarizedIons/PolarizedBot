package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
public class CommandPing implements ICommand {
    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "ping pong";
    }

    @Override
    public void exec(CommandMessage command) {
        command.replyLocalized("command.ping.reply", command.getAuthor().getPingString());
    }
}
