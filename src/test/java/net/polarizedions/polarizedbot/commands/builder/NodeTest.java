package net.polarizedions.polarizedbot.commands.builder;

import mocks.MockMessage;
import net.polarizedions.polarizedbot.exceptions.CommandExceptions;
import net.polarizedions.polarizedbot.exceptions.UnknownFail;
import net.polarizedions.polarizedbot.util.UserRank;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.obj.IMessage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    static Object getField(String field, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field f =  obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(obj);
    }

    static void setField(String field, Object value, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(obj, value);
    }

    static Method getMethod(String method, Object obj, Class<?>... args) throws NoSuchMethodException {
        Method m = obj.getClass().getDeclaredMethod(method, args);
        m.setAccessible(true);
        return m;
    }

    @Test
    void basic() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        CommandBuilder builder = CommandBuilder.create("test")
                .setRank(UserRank.GUILD_ADMIN);

        Node node = new Node(builder, null);

        // Test that rank is inherited
        assertEquals(UserRank.GUILD_ADMIN, getField("rank", node));

        // Test executable behaviour
        assertFalse(node.isExecutable());

        AtomicBoolean ran = new AtomicBoolean(false);
        Method runMethod = getMethod("run", node, IMessage.class, List.class);
        runMethod.invoke(node, new MockMessage(), Collections.emptyList());
        assertFalse(ran.get());

        node.onExecute((iMessage, objects) -> ran.set(true));

        assertTrue(node.isExecutable());


        runMethod.invoke(node, new MockMessage(), Collections.emptyList());

        assertTrue(ran.get());

        // Test fail behavior

        AtomicBoolean failed = new AtomicBoolean(false);
        Method failMethod = getMethod("fail", node, IMessage.class, List.class, List.class);
        assertThrows(UnknownFail.class, () -> {
            try {failMethod.invoke(node, new MockMessage(), Collections.emptyList(), Collections.emptyList());}
            catch (InvocationTargetException e) {throw e.getCause();}
        });
        assertFalse(failed.get());

        node.onFail((iMessage, objects, strings) -> failed.set(true));

        failMethod.invoke(node, new MockMessage(), Collections.emptyList(), Collections.emptyList());
        assertTrue(failed.get());

    }

    @Test
    void ping() throws CommandExceptions {
        AtomicBoolean pinged = new AtomicBoolean(false);
        CommandBuilder builder = CommandBuilder.create("ping");
        Node node = new Node(builder, null);
        node.onFail((iMessage, objects, strings) -> pinged.set(false));
        node.onExecute((iMessage, objects) -> pinged.set(false));

        node.stringArg("ping", n -> {
            n.onFail((iMessage, objects, strings) -> pinged.set(false));
            n.onExecute((iMessage, objects) -> pinged.set(true));
            pinged.set(true);
        });
        node.executeTree(new LinkedList<>(Arrays.asList("bla", "foo", "bar")), new MockMessage(), new LinkedList<>());
        assertFalse(pinged.get());

        node.executeTree(new LinkedList<>(Arrays.asList("ping", "foo", "bar")), new MockMessage(), new LinkedList<>());
        assertFalse(pinged.get());

        node.executeTree(new LinkedList<>(Collections.singletonList("ping")), new MockMessage(), new LinkedList<>());
        assertTrue(pinged.get());
    }
}