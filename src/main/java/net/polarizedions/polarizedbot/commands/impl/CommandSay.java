package net.polarizedions.polarizedbot.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.polarizedions.polarizedbot.commands.CommandResult;
import net.polarizedions.polarizedbot.commands.CommandSource;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.util.MessageUtil;
import sx.blah.discord.handle.obj.IChannel;

import java.util.Arrays;
import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.polarizedbot.commands.BrigadierTypeFixHelper.argument;
import static net.polarizedions.polarizedbot.commands.BrigadierTypeFixHelper.literal;
import static net.polarizedions.polarizedbot.commands.arguments.ChannelArgumentType.channel;
import static net.polarizedions.polarizedbot.commands.arguments.ChannelArgumentType.getChannel;

public class CommandSay implements ICommand {
    private LiteralCommandNode<CommandSource> SAY;

    @Override
    public void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
        SAY = dispatcher.register(literal("say")
            .then(
                    argument("channel", channel())
                        .then(
                                argument("text", greedyString())
                                    .executes(c -> say(getChannel(c, "channel"), getString(c, "text")))
                        )
            )
            .then(
                    argument("text", greedyString())
                        .executes(c -> say(c.getSource().channel, getString(c, "text")))
            )
        );

    }

    @Override
    public void help(CommandDispatcher<CommandSource> dispatcher, CommandSource source) {
        System.out.println(SAY.getUsageText());
        System.out.println(Arrays.toString(dispatcher.getAllUsage(SAY, source, false)));
        System.out.println(Arrays.toString(dispatcher.getAllUsage(SAY, source, true)));
        System.out.println();
        for (Map.Entry<CommandNode<CommandSource>, String> thing : dispatcher.getSmartUsage(SAY, source).entrySet())
        {
            System.out.println(thing.getKey() + " -- " + thing.getValue());
        }
    }

    private int say(IChannel target, String text) {
//        throw new RuntimeException("meow");
        MessageUtil.replyUnlocalized(target, text);
        return CommandResult.SUCCESS;
    }
}
