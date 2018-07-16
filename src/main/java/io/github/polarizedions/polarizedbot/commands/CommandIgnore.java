package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
import io.github.polarizedions.polarizedbot.wrappers.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.obj.IUser;

public class CommandIgnore implements ICommand {
    private static Logger logger = LogManager.getLogger("IgnoreCommand");

    @Override
    public String[] getCommand() {
        return new String[] {"ignore", "unignore"};
    }

    @Override
    public String getHelp() {
        return "ignores users";
    }

    @Override
    public void exec(CommandMessage command) {
        if (command.getCommand().equals("ignore")) {
            this.ignore(command);
        }
        else if (command.getCommand().equals("unignore")) {
            this.unignore(command);
        }
    }

    private void ignore(CommandMessage command) {
        User toIgnore = command.getAuthor();
        if (command.getArgs().length > 0) {
            if (command.getUserRank().rank < UserRank.GUILD_ADMIN.rank) {
                command.replyLocalized("command.ignore.error.no_manage_permission");
                return;
            }

            if (!command.getArgs()[0].startsWith("<@")) {
                command.replyLocalized("command.ignore.error.no_user");
                return;
            }

            String person = command.getArgs()[0].replaceAll("\\D+","");
            for (IUser user : command.getWrappedMessage().getMentions()) {
                if (user.getStringID().equals(person)) {
                    toIgnore = new User(user);
                    break;
                }
            }
        }

        if (toIgnore.getRank(command.getGuild()) == UserRank.BOT_OWNER) {
            command.replyLocalized("command.ignore.error.bot_owner");
            return;
        }

        logger.debug("Ignoring {}", toIgnore);
        command.getGuild().getConfig().ignoredUsers.add(toIgnore.getLongId());
        command.getGuild().saveConfig();
        command.replyLocalized("command.ignore.success");
    }

    private void unignore(CommandMessage command) {
        if (command.getUserRank().rank < UserRank.GUILD_ADMIN.rank) {
            command.replyLocalized("command.unignore.error.no_manage_permission");
            return;
        }

        if (command.getArgs().length == 0) {
            command.replyLocalized("command.unignore.error.no_user");
            return;
        }

        User toUnignore = null;
        String person = command.getArgs()[0].replaceAll("\\D+","");
        for (IUser user : command.getWrappedMessage().getMentions()) {
            if (user.getStringID().equals(person)) {
                toUnignore = new User(user);
                break;
            }
        }

        if (toUnignore == null) {
            command.replyLocalized("command.unignore.error.no_user");
            return;
        }

        logger.debug("Unignoring {}", toUnignore);
        command.getGuild().getConfig().ignoredUsers.remove(toUnignore.getLongId());
        command.getGuild().saveConfig();
        command.replyLocalized("command.unignore.success");
    }
}
