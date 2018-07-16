package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;

public class CommandShutdown implements ICommand {
    @Override
    public String[] getCommand() {
        return new String[] {"exit"};
    }

    @Override
    public String getHelp() {
        return "shut the bot down";
    }

    @Override
    public UserRank getRequiredRank() {
        return UserRank.BOT_OWNER;
    }

    @Override
    public void exec(CommandMessage command) {
        command.replyLocalized("command.shutdown.success");
        Bot.instance.shutdown();
    }
}
