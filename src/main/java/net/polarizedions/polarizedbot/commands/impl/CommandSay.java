package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.Localizer;
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
                                    IChannel channel = (IChannel)args.get(1);
                                    channel.sendMessage((String)args.get(2));
                                })
                        )
                        .swallow(false)
                        .onExecute((message, args) -> message.getChannel().sendMessage((String)args.get(1)))
                        .setHelp("command.say.help.say")
                )
                .command("tell", tell -> tell
                        .pingArg(pingNode -> pingNode
                                .swallow(false)
                                .onExecute((message, args) -> {
                                    IUser user = (IUser)args.get(1);
                                    user.getOrCreatePMChannel().sendMessage(new Localizer(message).localize("command.say.success", message.getAuthor().toString(), args.get(2)));
                                })
                        )
                        .setHelp("command.say.help.tell")
                )
                .setHelp("command.help.say")
                .buildCommand();
    }
}
