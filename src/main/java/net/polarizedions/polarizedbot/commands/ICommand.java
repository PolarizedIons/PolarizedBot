package net.polarizedions.polarizedbot.commands;

import com.mojang.brigadier.CommandDispatcher;

public interface ICommand {
    void registerCommand(CommandDispatcher<CommandSource> dispatcher);
    void help(CommandDispatcher<CommandSource> dispatcher, CommandSource source);
}