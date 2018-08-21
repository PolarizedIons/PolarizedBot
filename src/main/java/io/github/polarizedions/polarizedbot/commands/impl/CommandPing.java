package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import io.github.polarizedions.polarizedbot.util.Localizer;
import sx.blah.discord.handle.obj.IMessage;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CommandPing implements ICommand {

    public void run(IMessage message, List<Object> args) {
        String arg1 = (String) args.get(0);
        String replyKey = "command.ping.reply";
        if (arg1.equals("pong")) {
            replyKey += "_alt";
        }
        Instant ping = Instant.now();
        String msgText = Localizer.localize(replyKey + ".1", "<@!" + message.getAuthor().getLongID() + ">");

        IMessage msg = message.getChannel().sendMessage(msgText);

        Instant pong = msg.getTimestamp();
        Duration duration = Duration.between(ping, pong);
        String msgText2 = Localizer.localize(replyKey + ".2", "<@!" + message.getAuthor().getLongID() + ">", duration.toMillis());
        msg.edit(msgText2);
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Ping")
                .command("ping", "pong", ping -> ping.onExecute(this::run))
                .buildCommand();
    }
}
