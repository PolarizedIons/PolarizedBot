package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Message;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.MessageUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class CommandEightBall implements ICommand {
    private static final Random rand = new Random();
    private final Bot bot;

    public CommandEightBall(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("EightBall")
                .command("8ball", "eightball", command -> command
                        .swallow(false)
                        .onExecute(this::run)
                        .onFail(this::fail)
                )
                .buildCommand();
    }

    private void run(Message message, @NotNull ParsedArguments args) {
        if (!args.getAsString(1).endsWith("?")) {
            MessageUtil.reply(message, "command.8ball.error.no_question");
            return;
        }

        MessageUtil.reply(message, "command.8ball.answer." + (rand.nextInt(20) + 1));
    }

    private void fail(Message message, ParsedArguments parsedArgs, List<String> unparsedArgs) {
        MessageUtil.reply(message, "command.8ball.error.no_question");
    }
}
