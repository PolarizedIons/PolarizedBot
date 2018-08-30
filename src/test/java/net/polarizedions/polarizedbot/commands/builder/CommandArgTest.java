package net.polarizedions.polarizedbot.commands.builder;

import mocks.MockDiscordClient;
import net.polarizedions.polarizedbot.Bot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommandArgTest {

    @BeforeAll
    static void setupMocking() throws Exception {
        Class<Bot> clazz = Bot.class;
        Constructor<Bot> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Bot bot = constructor.newInstance();
        Field field = clazz.getDeclaredField("client");
        field.setAccessible(true);
        field.set(bot, new MockDiscordClient());
    }

    @Test
    void string() {
        CommandArg arg = CommandArg.String("bla");

        assertEquals("bla", arg.match("bla"));
        assertNull(arg.match("foobar"));
    }

    @Test
    void ping() {
        CommandArg arg = CommandArg.Ping();

        assertNull(arg.match("foobar"));
        assertNull(arg.match("123321"));
        assertNull(arg.match("<12344321>"));
        assertNull(arg.match("<#124425124213>"));
        assertNull(arg.match("<!12344321>"));
        assertEquals(123456789, ((IUser)arg.match("<@123456789>")).getLongID());
        assertEquals(123456789, ((IUser)arg.match("<@!123456789>")).getLongID());
    }

    @Test
    void channel() {
        CommandArg arg = CommandArg.Channel();

        assertNull(arg.match("foobar"));
        assertNull(arg.match("123321"));
        assertNull(arg.match("<12344321>"));
        assertNull(arg.match("<!12344321>"));
        assertNull(arg.match("<@123456789>"));
        assertNull(arg.match("<@!123456789>"));
        assertEquals(124425124213L, ((IChannel)arg.match("<#124425124213>")).getLongID());
    }

    @Test
    void any() {
        CommandArg arg = CommandArg.Any();

        assertEquals("bla", arg.match("bla"));
        assertEquals("123321", arg.match("123321"));
        assertEquals("<123456789>", arg.match("<123456789>"));
        assertEquals("<@123456789>", arg.match("<@123456789>"));
        assertEquals("<@!123456789>", arg.match("<@!123456789>"));
        assertEquals("<#124425124213>", arg.match("<#124425124213>"));

    }

    @Test
    void option() {
        CommandArg arg = CommandArg.Option(new String[] {"bla", "123321"});

        assertEquals("bla", arg.match("bla"));
        assertEquals("123321", arg.match("123321"));
        assertNull(arg.match("foobar"));
        assertNull(arg.match("<123456789>"));
        assertNull(arg.match("<@123456789>"));
        assertNull(arg.match("<@!123456789>"));
        assertNull(arg.match("<#124425124213>"));

    }

}