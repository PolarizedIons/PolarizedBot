package net.polarizedions.polarizedbot.commands.impl;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.UserRank;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandIgnore implements ICommand {
    private static final Logger logger = LogManager.getLogger("CommandIgnore");
    private final Bot bot;

    public CommandIgnore(Bot bot) {
        this.bot = bot;
    }

    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Ignore")
                .command("ignore", ignore ->
                        ignore.pingArg(pingNode -> pingNode
                                .rank(UserRank.LOCAL_ADMIN)
                                .onExecute(this::ignore)
                        )
                                .onFail(this::fail)
                                .onExecute(this::ignore)
                                .setHelp("command.ignore.help.ignore")
                )
                .command("unignore", unignore ->
                        unignore.pingArg(pingNode -> pingNode
                                .rank(UserRank.LOCAL_ADMIN)
                                .onExecute(this::unignore)
                        )
                                .onFail(this::fail)
                                .setHelp("command.ignore.help.unignore")
                )
                .setHelp("command.ignore.help")
                .buildCommand();
    }


    private void ignore(@NotNull Message message, @NotNull ParsedArguments args) {
        User toIgnore = args.size() == 1 ? message.getAuthor().get() : args.getAsUser(1);

        if (this.bot.getGuildManager().getUserRank(message.getGuild().block(), toIgnore) == UserRank.GLOBAL_ADMIN) {
            MessageUtil.reply(message, "command.ignore.error.global_admin");
            return;
        }

        logger.debug("Ignoring {}", toIgnore);
        this.bot.getGuildManager().getConfig(message.getGuild().block()).ignoredUsers.add(toIgnore.getId().asLong());
        this.bot.getGuildManager().saveConfig(message.getGuild().block());
        MessageUtil.reply(message, "command.ignore.success.ignore", toIgnore.getUsername() + "#" + toIgnore.getDiscriminator());
    }

    private void unignore(@NotNull Message message, @NotNull ParsedArguments args) {
        User toUnignore = args.getAsUser(1);

        logger.debug("Unignoring {}", toUnignore);
        this.bot.getGuildManager().getConfig(message.getGuild().block()).ignoredUsers.remove(toUnignore.getId().asLong());
        this.bot.getGuildManager().saveConfig(message.getGuild().block());
        MessageUtil.reply(message, "command.ignore.success.unignore", toUnignore.toString());
    }

    private void fail(Message message, ParsedArguments parsedArgs, List<String> unparsedArgs) {
        MessageUtil.reply(message, "command.ignore.error.no_user");
    }
}
