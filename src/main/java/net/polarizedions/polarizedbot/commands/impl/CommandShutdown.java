package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.UserRank;

public class CommandShutdown implements ICommand {

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Shutdown")
                .setHelp("Shuts the bot down")
                .setRank(UserRank.BOT_OWNER)
                .command("shutdown", shutdown -> shutdown.onExecute((message, args) -> {
                    logger.info("Shutting the bot down");
                    message.getChannel().setTypingStatus(false);
                    Bot.instance.shutdown();
                }))
                .buildCommand();
    }
}
