package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.util.GuildManager;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class CommandIgnore implements ICommand {
    private static final Logger logger = LogManager.getLogger("CommandIgnore");

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
                                .setHelp("command.ignore.help.ignore")
                )
                .command("unignore", unignore ->
                        unignore.pingArg(pingNode -> pingNode
                                .rank(UserRank.GUILD_ADMIN)
                                .onExecute(this::unignore)
                        )
                                .onFail(this::fail)
                                .setHelp("command.ignore.help.unignore")
                )
                .setHelp("command.ignore.help")
                .buildCommand();
    }


    private void ignore(@NotNull IMessage message, @NotNull List<Object> args) {
        IUser toIgnore = args.size() == 1 ? message.getAuthor() : (IUser)args.get(1);

        if (GuildManager.getUserRank(message.getGuild(), toIgnore) == UserRank.BOT_OWNER) {
            MessageUtil.reply(message, "command.ignore.error.bot_owner");
            return;
        }

        logger.debug("Ignoring {}", toIgnore);
        GuildManager.getConfig(message.getGuild()).ignoredUsers.add(toIgnore.getLongID());
        GuildManager.saveConfig(message.getGuild());
        MessageUtil.reply(message, "command.ignore.success.ignore", toIgnore.getName() + "#" + toIgnore.getDiscriminator());
    }

    private void unignore(@NotNull IMessage message, @NotNull List<Object> args) {
        IUser toUnignore = (IUser)args.get(1);

        logger.debug("Unignoring {}", toUnignore);
        GuildManager.getConfig(message.getGuild()).ignoredUsers.remove(toUnignore.getLongID());
        GuildManager.saveConfig(message.getGuild());
        MessageUtil.reply(message, "command.ignore.success.unignore", toUnignore.toString());
    }

    private void fail(IMessage message, List<Object> parsedArgs, List<String> unparsedArgs) {
        MessageUtil.reply(message, "command.ignore.error.no_user");
    }
}
