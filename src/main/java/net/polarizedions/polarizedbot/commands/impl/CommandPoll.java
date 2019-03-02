package net.polarizedions.polarizedbot.commands.impl;

import net.polarizedions.polarizedbot.commands.ICommand;
import net.polarizedions.polarizedbot.commands.builder.CommandBuilder;
import net.polarizedions.polarizedbot.commands.builder.CommandTree;
import net.polarizedions.polarizedbot.commands.builder.ParsedArguments;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.obj.IMessage;

import java.util.ArrayList;
import java.util.List;

public class CommandPoll implements ICommand {
    @Override
    public CommandTree getCommand() {
        return CommandBuilder.create("Poll")
                .command("poll", poll ->
                    poll
                        .swallow(false)
                        .onExecute(this::run)
                        .onFail(this::fail)
                )
                .buildCommand();
    }

    private void fail(IMessage message, @NotNull ParsedArguments args, List<String> nonParsedArgs) {
        if (args.size() == 1) {
            MessageUtil.reply(message, "command.poll.error.no_question");
        }
    }

    private void run(IMessage message, @NotNull ParsedArguments args) {
        System.out.println("RUNNING POLL " + args);
        Pair<String, List<String>> parsedPoll = this.parsePollQuestion(message, args.getAsString(1));

        if (parsedPoll == null) {
            return;
        }

        MessageUtil.reply(message, "QUESTION: " + parsedPoll.one + " OPTIONS: " + parsedPoll.two);
    }

    @Nullable
    private Pair<String, List<String>> parsePollQuestion(@NotNull IMessage message, @NotNull String input) {
        Localizer loc = new Localizer(message);
        int questionMarkIndex = input.indexOf("?");

        if (questionMarkIndex == -1) {
            MessageUtil.reply(message, "command.poll.error.no_questionmark");
            return null;
        }

        if (input.length() <= questionMarkIndex) {
            MessageUtil.reply(message, "command.poll.no_options");
            return null;
        }

        String questionText = input.substring(0, questionMarkIndex).trim();
        String optionsText = input.substring(questionMarkIndex + 1);
        if (questionText.isEmpty()) {
            MessageUtil.reply(message, "command.poll.no_question");
            return null;
        }

        List<String> options = new ArrayList<>();
        for (String option : optionsText.split("\\|")) {
            option = option.trim();
            if (! option.isEmpty()) {
                options.add(option);
            }
        }

        if (options.size() == 0) {
            MessageUtil.reply(message, "command.poll.no_options");
            return null;
        }
        else if (options.size() == 1) {
            MessageUtil.reply(message, "command.poll.error.one_option");
            return null;
        }

        return new Pair<>(questionText, options);
    }
}
