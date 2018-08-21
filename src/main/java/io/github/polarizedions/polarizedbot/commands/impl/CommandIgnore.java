package io.github.polarizedions.polarizedbot.commands.impl;

import io.github.polarizedions.polarizedbot.commands.ICommand;
import io.github.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import io.github.polarizedions.polarizedbot.commands.builder.CommandTree;
import io.github.polarizedions.polarizedbot.util.GuildManager;
import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.util.UserRank;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class CommandIgnore implements ICommand {

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Ignore")
                .command("ignore", ignore ->
                        ignore.pingArg(pingNode -> pingNode
                                .rank(UserRank.GUILD_ADMIN)
                                .onExecute(this::ignore)
                        )
                        .onFail(this::fail)
                        .onExecute(this::ignore)
                )
                .command("unignore", unignore ->
                    unignore.pingArg(pingNode -> pingNode
                            .rank(UserRank.GUILD_ADMIN)
                            .onExecute(this::unignore)
                    )
                    .onFail(this::fail)
                )
                .buildCommand();
    }


    private void ignore(IMessage message, List<Object> args) {
        IUser toIgnore = args.size() == 1 ? message.getAuthor() : (IUser) args.get(1);

        if (GuildManager.getUserRank(message.getGuild(), toIgnore) == UserRank.BOT_OWNER) {
            message.getChannel().sendMessage(Localizer.localize("command.ignore.error.bot_owner"));
            return;
        }

        logger.debug("Ignoring {}", toIgnore);
        GuildManager.getConfig(message.getGuild()).ignoredUsers.add(toIgnore.getLongID());
        GuildManager.saveConfig(message.getGuild());
        message.getChannel().sendMessage(Localizer.localize("command.ignore.success.ignore", toIgnore.getName()+"#"+toIgnore.getDiscriminator()));
    }

    private void unignore(IMessage message, List<Object> args) {
        IUser toUnignore = (IUser) args.get(1);

        logger.debug("Unignoring {}", toUnignore);
        GuildManager.getConfig(message.getGuild()).ignoredUsers.remove(toUnignore.getLongID());
        GuildManager.saveConfig(message.getGuild());
        message.getChannel().sendMessage(Localizer.localize("command.ignore.success.unignore", toUnignore.toString()));

    }

    private void fail(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        message.getChannel().sendMessage(Localizer.localize("command.ignore.error.no_user"));
    }
}
