package io.github.polarizedions.polarizedbot.commands.command_builder;

import java.util.function.BiFunction;

public class Option {
    private final String option;
    private final BiFunction<String, String, Boolean> matchingFunc;

    public Option(String option, BiFunction<String, String, Boolean> matchingFunc) {
        this.option = option;
        this.matchingFunc = matchingFunc;
    }

    public boolean match(String commandPart) {
        return this.matchingFunc.apply(this.option, commandPart);
    }

    public static Option String(String option) {
        return new Option(option, String::equalsIgnoreCase);
    }

    public static Option Ping() {
        return new Option("{PingMatch}", (a, in) -> in.startsWith("<@"));
    }

    @Override
    public String toString() {
        return "Option<" + this.option + ">";
    }
}
