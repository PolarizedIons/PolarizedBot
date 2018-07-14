package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
import io.github.polarizedions.polarizedbot.wrappers.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.HashMap;

public class CommandManager {
    private Logger logger = LogManager.getLogger("CommandManager");
    private HashMap<String, ICommand> commands = new HashMap<>();

    public CommandManager() {
        this.registerCommand(new CommandPing());
        this.registerCommand(new CommandGuild());
        this.registerCommand(new CommandAnnoucer());
        this.registerCommand(new CommandShutdown());
    }

    public void registerCommand(ICommand command) {
        this.commands.put(command.getCommand(), command);
    }

    public void registerListeners(IDiscordClient discordClient) {
        discordClient.getDispatcher().registerListener((IListener<MessageReceivedEvent>) this::messageHandler);
    }

    public void messageHandler(MessageReceivedEvent event) {
        Message message = new Message(event.getMessage());
        logger.debug("Received {}", message);

        if (!message.isCommand()) {
            return;
        }

        CommandMessage commandMsg = message.getAsCommand();
        if (commandMsg.getUserRank() == UserRank.IGNORED) {
            return;
        }

        if (commandMsg.getGuild().getConfig().disabledCommands.contains(commandMsg.getCommand())) {
            commandMsg.replyLocalized("error.command.disabled");
            return;
        }

        ICommand command = commands.get(commandMsg.getCommand());
        if (command == null) {
            commandMsg.replyLocalized("error.command.not_found");
            return;
        }

        if (command.getRequiredRank().rank > commandMsg.getUserRank().rank) {
            commandMsg.replyLocalized("error.command.no_permission");
            return;
        }

        logger.debug("Running command {}", commandMsg.getCommand());
        command.exec(commandMsg);
    }
}
