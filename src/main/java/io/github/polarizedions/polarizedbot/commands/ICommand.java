package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
import io.github.polarizedions.polarizedbot.wrappers.Guild;

public interface ICommand {
    String getCommand();

    String getHelp();

    default UserRank getRequiredRank() {
        return UserRank.DEFAULT;
    }

    void exec(CommandMessage command);
}
