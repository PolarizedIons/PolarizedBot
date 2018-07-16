package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;

public interface ICommand {
    String[] getCommand();

    String getHelp();

    default UserRank getRequiredRank() {
        return UserRank.DEFAULT;
    }

    void exec(CommandMessage command);
}
