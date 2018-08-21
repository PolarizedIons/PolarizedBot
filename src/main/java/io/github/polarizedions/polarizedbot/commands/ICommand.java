package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface ICommand {
    Logger logger = LogManager.getLogger(ICommand.class.getSimpleName());

    CommandTree getCommand();
}
