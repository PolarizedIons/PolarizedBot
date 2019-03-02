package net.polarizedions.polarizedbot.commands.builder;

import mocks.SetupMocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommandArgTest {

    @BeforeAll
    static void setupMocking() throws Exception {
        SetupMocks.botClient();
    }

    @Test
    void string() {
        CommandArg arg = CommandArg.string("bla");

        assertEquals("bla", arg.match("bla"));
        assertNull(arg.match("foobar"));
    }

    @Test
    void ping() {
        CommandArg arg = CommandArg.ping();

        assertNull(arg.match("foobar"));
        assertNull(arg.match("123321"));
        assertNull(arg.match("<12344321>"));
        assertNull(arg.match("<#124425124213>"));
        assertNull(arg.match("<!12344321>"));
        assertEquals(123456789, ( (IUser)arg.match("<@123456789>") ).getLongID());
        assertEquals(123456789, ( (IUser)arg.match("<@!123456789>") ).getLongID());
    }

    @Test
    void channel() {
        CommandArg arg = CommandArg.channel();

        assertNull(arg.match("foobar"));
        assertNull(arg.match("123321"));
        assertNull(arg.match("<12344321>"));
        assertNull(arg.match("<!12344321>"));
        assertNull(arg.match("<@123456789>"));
        assertNull(arg.match("<@!123456789>"));
        assertEquals(124425124213L, ( (IChannel)arg.match("<#124425124213>") ).getLongID());
    }

    @Test
    void any() {
        CommandArg arg = CommandArg.any();

        assertEquals("bla", arg.match("bla"));
        assertEquals("123321", arg.match("123321"));
        assertEquals("<123456789>", arg.match("<123456789>"));
        assertEquals("<@123456789>", arg.match("<@123456789>"));
        assertEquals("<@!123456789>", arg.match("<@!123456789>"));
        assertEquals("<#124425124213>", arg.match("<#124425124213>"));

    }

    @Test
    void option() {
        CommandArg arg = CommandArg.option(new String[] { "bla", "123321" });

        assertEquals("bla", arg.match("bla"));
        assertEquals("123321", arg.match("123321"));
        assertNull(arg.match("foobar"));
        assertNull(arg.match("<123456789>"));
        assertNull(arg.match("<@123456789>"));
        assertNull(arg.match("<@!123456789>"));
        assertNull(arg.match("<#124425124213>"));

    }

}