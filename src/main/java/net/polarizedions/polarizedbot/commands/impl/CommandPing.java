package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.Localizer;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.Color;
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
        this.run(message, "1");
    }

    void pong(IMessage message, ParsedArguments args) {
        this.run(message, "2");
    }

    void run(IMessage message, String replyKey) {
        Localizer loc = new Localizer(message);

        EmbedBuilder initialEmbedBuilder = new EmbedBuilder();
        initialEmbedBuilder.withColor(new Color(188, 198, 51));
        initialEmbedBuilder.withTitle(loc.localize("command.ping.title." + replyKey));
        initialEmbedBuilder.appendField(loc.localize("command.ping.content." + replyKey), loc.localize("command.ping.content.tbd"), true);

        EmbedBuilder finalEmbedBuilder = new EmbedBuilder();
        finalEmbedBuilder.withColor(new Color(31, 192, 62));
        finalEmbedBuilder.withTitle(loc.localize("command.ping.title." + replyKey));



        Instant ping = Instant.now();

        RequestBuffer.request(() -> {
            IMessage msg = message.getChannel().sendMessage(initialEmbedBuilder.build());

            Instant pong = Instant.now();
            Duration duration = Duration.between(ping, pong);

            finalEmbedBuilder.appendField(loc.localize("command.ping.content." + replyKey), loc.localize("command.ping.reply", duration.toMillis()), true);

            RequestBuffer.request(() -> {
                msg.edit(finalEmbedBuilder.build());
            });
        });
    }
}
