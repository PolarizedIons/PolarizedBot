package io.github.polarizedions.polarizedbot.commands.builder;

import io.github.polarizedions.polarizedbot.Bot;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandArg {
    private static final Pattern USER_PING_PATTERN = Pattern.compile("<@!?([0-9]+)>");
    private static final Pattern CHANNEL_PATTERN = Pattern.compile("<#([0-9]+)>");

    private final Function<String, Object> matchingFunc;

    public CommandArg(Function<String, Object> matchingFunc) {
        this.matchingFunc = matchingFunc;
    }

    public Object match(String commandPart) {
        return this.matchingFunc.apply(commandPart);
    }

    public static CommandArg String(String option) {
        return new CommandArg((in) -> {
            if (option.equalsIgnoreCase(in)) {
                return in;
            }
            return null;
        });
    }

    public static CommandArg Ping() {
        return new CommandArg((in) -> {
            Matcher m = USER_PING_PATTERN.matcher(in);
            if (m.matches()) {
                return Bot.instance.getClient().getUserByID(Long.parseLong(m.group(1)));
            }

            return null;
        });
    }

    public static CommandArg Channel() {
        return new CommandArg((in) -> {
            Matcher m = CHANNEL_PATTERN.matcher(in);
            if (m.matches()) {
                return Bot.instance.getClient().getChannelByID(Long.parseLong(m.group(1)));
            }

            return null;
        });
    }

    public static CommandArg Any() {
        return new CommandArg((in) -> {
            return in.isEmpty() ? null : in;
        });
    }

    public static CommandArg Option(String[] options) {
        return new CommandArg((in) -> {
            for (String option : options) {
                if (option.equalsIgnoreCase(in)) {
                    return in;
                }
            }
            return null;
        });
    }
}
