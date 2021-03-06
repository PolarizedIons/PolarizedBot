package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.MessageUtil;
import sx.blah.discord.handle.obj.IChannel;

public class CommandSay implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Say")
                .command("say", say -> say
                        .channelArg(channelNode -> channelNode
                                .swallow(false)
                                .onExecute((message, args) -> {
                                    IChannel channel = args.getAsChannel(1);
                                    channel.sendMessage(args.getAsString(2));
                                })
                        )
                        .swallow(false)
                        .onExecute((message, args) -> MessageUtil.replyUnlocalized(message.getChannel(), args.getAsString(1)))
                        .setHelp("command.say.help.say")
                )
                .command("tell", tell -> tell
                        .pingArg(pingNode -> pingNode
                                .swallow(false)
                                .onExecute((message, args) -> {
                                    IChannel channel = args.getAsUser(1).getOrCreatePMChannel();
                                    MessageUtil.reply(channel, "command.say.success", message.getAuthor().mention(), args.getAsString(2));
                                })
                        )
                        .setHelp("command.say.help.tell")
                )
                .setHelp("command.help.say")
                .buildCommand();
    }
}
