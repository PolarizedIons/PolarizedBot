package net.polarizedions.polarizedbot.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.polarizedions.polarizedbot.commands.CommandResult;
import net.polarizedions.polarizedbot.commands.CommandSource;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.util.Localizer;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;

import java.time.Duration;
import java.time.Instant;

import static net.polarizedions.polarizedbot.commands.BrigadierTypeFixHelper.literal;


public class CommandPing implements ICommand {

//    @Override
//    public CommandTree getCommand() {
//        return CommandBuilder.create("Ping")
//                .command("ping", ping -> ping
//                        .onExecute(this::ping)
//                        .setHelp("command.ping.help.pingpong")
//                )
//                .command("pong", pong -> pong
//                        .onExecute(this::pong)
//                        .setHelp("command.ping.help.pingpong")
//                )
//                .setHelp("command.ping.help")
//                .buildCommand();
//    }
//
//    void ping(IMessage message, ParsedArguments args) {
//        this.run(message, "command.ping.reply");
//    }
//
//    void pong(IMessage message, ParsedArguments args) {
//        this.run(message, "command.ping.reply_alt");
//    }

    void run(IMessage message, String replyKey) {
        Localizer loc = new Localizer(message);

        Instant ping = Instant.now();
        String initialText = loc.localize(replyKey + ".1", message.getAuthor().mention());

        IMessage msg = message.getChannel().sendMessage(initialText);

        Instant pong = Instant.now();
        Duration duration = Duration.between(ping, pong);
        String latencyText = loc.localize(replyKey + ".2", message.getAuthor().mention(), duration.toMillis());
        msg.edit(latencyText);
    }

    @Override
    public void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("ping").executes(this::ping));
        dispatcher.register(literal("pong").executes(this::pong));
    }

    @Override
    public void help(CommandDispatcher<CommandSource> dispatcher, CommandSource source) {

    }

    private int ping(@NotNull CommandContext<CommandSource> context) {
        this.run(context.getSource().message, "command.ping.reply");
        return CommandResult.SUCCESS;
    }

    private int pong(@NotNull CommandContext<CommandSource> context) {
        this.run(context.getSource().message, "command.ping.reply_alt");
        return CommandResult.SUCCESS;
    }


}
