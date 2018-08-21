package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class CommandSay implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Say")
                .command("say", say -> say
                    .channelArg(channelNode -> channelNode
                            .swallow(false)
                            .onExecute((message, args) -> {
                                IChannel channel = (IChannel) args.get(1);
                                channel.sendMessage((String) args.get(2));
                            }))
                    .swallow(false)
                    .onExecute((message, args) -> message.getChannel().sendMessage((String)args.get(1)))
                )
                .command("tell", tell -> tell
                    .pingArg(pingNode -> pingNode
                        .swallow(false)
                        .onExecute((message, args) -> {
                            IUser user = (IUser) args.get(1);
                            user.getOrCreatePMChannel().sendMessage(message.getAuthor().toString() + "says: " + args.get(2));
                        }))
                )
                .buildCommand();
    }
}
