package net.polarizedions.polarizedbot.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.polarizedbot.util.UserRank;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class UserRankArgumentType implements ArgumentType<UserRank> {
    @Override
    public <S> UserRank parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        String word = reader.readUnquotedString().toLowerCase();
        UserRank rank = UserRank.getByName(word);

        if (rank == null) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        }

        return rank;
    }

    @NotNull
    @Contract(" -> new")
    public static UserRankArgumentType userRank() {
        return new UserRankArgumentType();
    }

    public static UserRank getRankUser(@NotNull final CommandContext<?> context, final String name) {
        return context.getArgument(name, UserRank.class);
    }

    @Override
    public String toString() {
        return "userRank()";
    }
}
