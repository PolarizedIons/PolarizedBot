package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.Localizer;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.time.Duration;
import java.time.Instant;

public class CommandPing implements ICommand {

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Ping")
                .command("ping", ping -> ping
                        .onExecute(this::ping)
                        .setHelp("command.ping.help.pingpong")
                )
                .command("pong", pong -> pong
                        .onExecute(this::pong)
                        .setHelp("command.ping.help.pingpong")
                )
                .setHelp("command.ping.help")
                .buildCommand();
    }

    void ping(IMessage message, ParsedArguments args) {
        this.run(message, "command.ping.reply");
    }

    void pong(IMessage message, ParsedArguments args) {
        this.run(message, "command.ping.reply_alt");
    }

    void run(IMessage message, String replyKey) {
        Localizer loc = new Localizer(message);

        Instant ping = Instant.now();
        String initialText = loc.localize(replyKey + ".1", message.getAuthor().mention());

        RequestBuffer.request(() -> {
            IMessage msg = message.getChannel().sendMessage(initialText);

            Instant pong = Instant.now();
            Duration duration = Duration.between(ping, pong);
            String latencyText = loc.localize(replyKey + ".2", message.getAuthor().mention(), duration.toMillis());

            RequestBuffer.request(() -> {
                msg.edit(latencyText);
            });
        });
    }
}
