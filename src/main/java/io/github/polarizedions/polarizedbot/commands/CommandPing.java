package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
import sx.blah.discord.handle.obj.IMessage;

import java.time.Duration;
import java.time.Instant;

public class CommandPing implements ICommand {
    private Localizer localizer;

    public CommandPing() {
        this.localizer = Bot.instance.getLocalizer();
    }

    @Override
    public String[] getCommand() {
        return new String[] {"ping", "pong"};
    }

    @Override
    public String getHelp() {
        return "ping pong";
    }

    @Override
    public void exec(CommandMessage command) {
        String replyKey = "command.ping.reply";
        if (command.getCommand().equals("pong")) {
            replyKey += "_alt";
        }
        Instant ping = Instant.now();
//        command.replyLocalized("command.ping.reply", command.getAuthor().getPingString());
        String msgText = localizer.localize(command.getGuild(), replyKey + ".1", command.getAuthor().getPingString());

        IMessage msg = command.getWrappedMessage().getChannel().sendMessage(msgText);

        Instant pong = msg.getTimestamp();
        Duration duration = Duration.between(ping, pong);
        String msgText2 = localizer.localize(command.getGuild(), replyKey + ".2", command.getAuthor().getPingString(), duration.toMillis());
        msg.edit(msgText2);
    }
}
