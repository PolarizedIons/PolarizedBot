package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.TextChannel;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.MessageUtil;

public class CommandSay implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Say")
                .command("say", say -> say
                        .channelArg(channelNode -> channelNode
                                .swallow(false)
                                .onExecute((message, args) -> {
                                    TextChannel channel = args.getAsChannel(1);
                                    channel.createMessage(args.getAsString(2));
                                })
                        )
                        .swallow(false)
                        .onExecute((message, args) -> message.getChannel().block().createMessage(args.getAsString(1)))
                        .setHelp("command.say.help.say")
                )
                .command("tell", tell -> tell
                        .pingArg(pingNode -> pingNode
                                .swallow(false)
                                .onExecute((message, args) -> {
                                    args.getAsUser(1).getPrivateChannel().subscribe(channel ->
                                            MessageUtil.reply(channel, "command.say.success", message.getAuthor().get().getMention(), args.getAsString(2))
                                    );
                                })
                        )
                        .setHelp("command.say.help.tell")
                )
                .setHelp("command.help.say")
                .buildCommand();
    }
}
