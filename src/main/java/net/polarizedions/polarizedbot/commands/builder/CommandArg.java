package net.polarizedions.polarizedbot.commands.builder;

import discord4j.core.object.util.Snowflake;
import net.polarizedions.polarizedbot.Bot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Contract("_ -> new")
    public static CommandArg string(String option) {
        return new CommandArg((in) -> {
            if (option.equalsIgnoreCase(in)) {
                return in;
            }

            return null;
        });
    }

    @NotNull
    @Contract(" -> new")
    public static CommandArg ping(Bot bot) {
        return new CommandArg((in) -> {
            Matcher m = USER_PING_PATTERN.matcher(in);
            if (m.matches()) {
                return bot.getClient().getUserById(Snowflake.of(Long.parseLong(m.group(1))));
            }

            return null;
        });
    }

    @NotNull
    @Contract(" -> new")
    public static CommandArg channel(Bot bot) {
        return new CommandArg((in) -> {
            Matcher m = CHANNEL_PATTERN.matcher(in);
            if (m.matches()) {
                return bot.getClient().getChannelById(Snowflake.of(Long.parseLong(m.group(1))));
            }

            return null;
        });
    }

    @NotNull
    @Contract(" -> new")
    public static CommandArg any() {
        return new CommandArg((in) -> in.isEmpty() ? null : in);
    }

    @NotNull
    @Contract("_ -> new")
    public static CommandArg option(String[] options) {
        return new CommandArg((in) -> {
            for (String option : options) {
                if (option.equalsIgnoreCase(in)) {
                    return in;
                }
            }

            return null;
        });
    }

    public Object match(String commandPart) {
        return this.matchingFunc.apply(commandPart);
    }
}
