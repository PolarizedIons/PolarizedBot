package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Message;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.Localizer;

import java.awt.Color;
import java.time.Duration;
import java.time.Instant;

public class CommandPing implements ICommand {
    private final Bot bot;

    public CommandPing(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create(bot, "Ping")
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

    void ping(Message message, ParsedArguments args) {
        this.run(message, "1");
    }

    void pong(Message message, ParsedArguments args) {
        this.run(message, "2");
    }

    void run(Message message, String replyKey) {
        Localizer loc = new Localizer(bot.getGuildManager().getConfig(message.getGuild().block()).lang);

        Instant ping = Instant.now();

        message.getChannel().subscribe(channel -> {
            channel.createMessage(msgSpec -> {
                msgSpec.setEmbed(embedSpec -> {
                    embedSpec.setColor(new Color(188, 198, 51));
                    embedSpec.setTitle(loc.localize("command.ping.title." + replyKey));
                    embedSpec.addField(loc.localize("command.ping.content." + replyKey), loc.localize("command.ping.content.tbd"), true);
                });
            }).subscribe(msgSent -> msgSent.edit(msg -> {
                Instant pong = Instant.now();
                Duration duration = Duration.between(ping, pong);

                msg.setEmbed(embedSpec -> {
                   embedSpec.setColor(new Color(31, 192, 62));
                   embedSpec.setTitle(loc.localize("command.ping.title." + replyKey));
                   embedSpec.addField(loc.localize("command.ping.content." + replyKey), loc.localize("command.ping.reply", duration.toMillis()), true);
               });
            }).block());
        });
    }
}
