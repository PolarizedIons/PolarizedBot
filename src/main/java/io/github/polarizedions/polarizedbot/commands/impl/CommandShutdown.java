package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import io.github.polarizedions.polarizedbot.util.UserRank;

public class CommandShutdown implements ICommand {

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Shutdown")
                .setHelp("Shuts the bot down")
                .setRank(UserRank.BOT_OWNER)
                .command("shutdown", shutdown -> shutdown.onExecute((message, args) -> {
                    logger.info("Shutting the bot down");
                    Bot.instance.shutdown();
                }))
                .buildCommand();
    }
}
