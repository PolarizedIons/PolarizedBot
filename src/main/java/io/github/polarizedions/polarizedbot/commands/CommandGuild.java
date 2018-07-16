package io.github.polarizedions.polarizedbot.commands;

import io.github.polarizedions.polarizedbot.Bot;
import io.github.polarizedions.polarizedbot.util.Localizer;
import io.github.polarizedions.polarizedbot.util.UserRank;
import io.github.polarizedions.polarizedbot.wrappers.CommandMessage;
import io.github.polarizedions.polarizedbot.wrappers.User;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommandGuild implements ICommand {

    private static Map<String, Consumer<CommandMessage>> subcommands;

    public CommandGuild() {
        subcommands = new HashMap<>();
        subcommands.put("set-lang", this::setLang);
        subcommands.put("set-rank", this::setRank);
        subcommands.put("disable", this::disableCommand);
        subcommands.put("enable", this::enableCommand);
        subcommands.put("set-prefix", this::setPrefix);
    }

    @Override
    public String[] getCommand() {
        return new String[] {"guild"};
    }

    @Override
    public String getHelp() {
        return "manage your guild";
    }

    @Override
    public UserRank getRequiredRank() {
        return UserRank.GUILD_ADMIN;
    }

    @Override
    public void exec(CommandMessage command) {
        if (command.getArgs().length == 0) {
            command.replyLocalized("command.guild.error.no_subcommand", String.join(", ", subcommands.keySet()));
            return;
        }
        String subcommand = command.getArgs()[0];
        Consumer<CommandMessage> consumer = subcommands.getOrDefault(subcommand, this::unknownSubcommand);
        consumer.accept(command);
    }

    private void unknownSubcommand(CommandMessage command) {
        command.replyLocalized("command.guild.error.unknown_subcommand", command.getArgs()[0], String.join(", ", subcommands.keySet()));
    }

    private void setLang(CommandMessage command) {
        if (command.getArgs().length < 2) {
            command.replyLocalized("command.guild.error.not_enough_args");
            return;
        }

        String newLang = command.getArgs()[1];
        Localizer localizer = Bot.instance.getLocalizer();
        if (!localizer.supports(newLang)) {
            command.replyLocalized("command.guild.error.unknown_lang", newLang);
            return;
        }

        command.getGuild().getConfig().lang = newLang;
        command.getGuild().saveConfig();
        command.replyLocalized("command.guild.success.setlang", newLang);
    }

    private void setRank(CommandMessage command) {
        if (command.getArgs().length < 3) {
            command.replyLocalized("command.guild.error.not_enough_args");
            return;
        }

        String rankName = command.getArgs()[1];
        String person = command.getArgs()[2];

        UserRank rank = UserRank.getByName(rankName);
        if (rank == null) {
            command.replyLocalized("command.guild.error.no_such_rank", rankName, String.join(", ", UserRank.getNames()));
            return;
        }

        if (!person.startsWith("<@")) {
            command.replyLocalized("command.guild.error.no_such_person");
            return;
        }

        person = person.replaceAll("\\D+","");
        IUser user = null;
        for (IUser mentionedUser : command.getWrappedMessage().getMentions()) {
            if (mentionedUser.getStringID().equals(person)) {
                user = mentionedUser;
                break;
            }
        }

        if (user == null) {
            command.replyLocalized("command.guild.error.no_such_person");
            return;
        }

        User myUser = new User(user);
        command.getGuild().getConfig().setRank(myUser, rank);
        command.getGuild().saveConfig();
        command.replyLocalized("command.guild.success.set_rank", myUser.getPingString(), rank.toString());
    }

    private void enableCommand(CommandMessage command) {
        if (command.getArgs().length < 2) {
            command.replyLocalized("command.guild.error.not_enough_args");
            return;
        }

        // TODO: validate command names
        command.getGuild().getConfig().disabledCommands.remove(command.getArgs()[1]);
        command.getGuild().saveConfig();
        command.replyLocalized("command.guild.success.enable", command.getArgs()[1]);
    }

    private void disableCommand(CommandMessage command) {
        if (command.getArgs().length < 2) {
            command.replyLocalized("command.guild.error.not_enough_args");
            return;
        }

        // TODO: validate command names
        command.getGuild().getConfig().disabledCommands.add(command.getArgs()[1]);
        command.getGuild().saveConfig();
        command.replyLocalized("command.guild.success.disable", command.getArgs()[1]);
    }

    private void setPrefix(CommandMessage command) {
        if (command.getArgs().length < 2) {
            command.replyLocalized("command.guild.error.not_enough_args");
            return;
        }

        // TODO: does this need validation?
        command.getGuild().getConfig().commandPrefix = command.getArgs()[1];
        command.getGuild().saveConfig();
        command.replyLocalized("command.guild.success.set_prefix", command.getArgs()[1]);
    }
}
