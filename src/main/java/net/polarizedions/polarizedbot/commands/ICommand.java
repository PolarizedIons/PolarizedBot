package net.polarizedions.polarizedbot.commands;

import net.polarizedions.polarizedbot.commands.builder.CommandTree;

public interface ICommand {
    CommandTree getCommand();
}
