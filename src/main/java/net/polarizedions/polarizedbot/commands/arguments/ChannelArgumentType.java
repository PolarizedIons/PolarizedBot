package net.polarizedions.polarizedbot.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.polarizedbot.Bot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;

public class ChannelArgumentType implements ArgumentType<IChannel> {

    @Override
    public <S> IChannel parse(StringReader reader) throws CommandSyntaxException {
        System.out.println("parsing channel...");
        reader.expect('<');
        System.out.println("found <");
        reader.expect('#');
        System.out.println("FOUND #");

        // TODO: update when .readLong becomes a thing
        long id = 0;
        // copied from readInt
        final int start = reader.getCursor();
        while (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
            reader.skip();
        }
        final String number = reader.getString().substring(start, reader.getCursor());
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt().createWithContext(reader);
        }
        try {
            id = Long.parseLong(number);
        } catch (final NumberFormatException ex) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, number);
        }


        System.out.println("read long " + id);
        reader.expect('>');
        System.out.println("found >");

        return Bot.instance.getClient().getChannelByID(id);
    }

    @NotNull
    @Contract(" -> new")
    public static ChannelArgumentType channel() {
        return new ChannelArgumentType();
    }

    public static IChannel getChannel(@NotNull final CommandContext<?> context, final String name) {
        return context.getArgument(name, IChannel.class);
    }

    @Override
    public String toString() {
        return "channel()";
    }
}
