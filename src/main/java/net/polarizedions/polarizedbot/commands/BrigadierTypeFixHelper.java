package net.polarizedions.polarizedbot.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

public class BrigadierTypeFixHelper {
    // Thank you pokechu22 in #mcdevs on Freenode for helping me figure this out

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * LiteralArgumentBuilder#literal} method is that it is typed to {@link CommandSource}.
     */
    public static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.<CommandSource>literal(name);
    }

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * RequiredArgumentBuilder#argument} method is that it is typed to {@link CommandSource}.
     */
    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.<CommandSource, T>argument(name, type);
    }
}
