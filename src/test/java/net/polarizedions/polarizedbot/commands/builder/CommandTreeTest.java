package net.polarizedions.polarizedbot.commands.builder;

import mocks.MockDiscordClient;
import mocks.MockMessage;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.exceptions.CommandExceptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.obj.IMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandTreeTest {

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
    void pingPong() throws CommandExceptions {
        AtomicBoolean pinged = new AtomicBoolean(false);
        AtomicBoolean ponged = new AtomicBoolean(false);
        AtomicBoolean pingedPerson = new AtomicBoolean(false);

        CommandTree tree = CommandBuilder.create("ping")
                .command("ping", p -> p
                        .pingArg(u -> u.onExecute((iMessage, objects) -> pingedPerson.set(true)))
                        .onExecute((iMessage, objects) -> pinged.set(true))
                )
                .command("pong", p -> p
                        .onExecute((iMessage, objects) -> ponged.set(true))
                )
                .buildCommand();

        assertFalse(pinged.get());
        assertFalse(ponged.get());
        assertFalse(pingedPerson.get());

        IMessage msg = new MockMessage();

        tree.execute(new LinkedList<>(Arrays.asList("ping")), msg);
        tree.execute(new LinkedList<>(Arrays.asList("ping", "<@123321>")), msg);
        tree.execute(new LinkedList<>(Arrays.asList("pong")), msg);

        assertTrue(pinged.get());
        assertTrue(ponged.get());
        assertTrue(pingedPerson.get());
    }
}