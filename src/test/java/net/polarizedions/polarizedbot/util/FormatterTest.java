package net.polarizedions.polarizedbot.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatterTest {

    @SuppressWarnings("unchecked")
    Map<String, Supplier<String>> makeArgsAccesable(Formatter formatter) throws NoSuchFieldException, IllegalAccessException {
        Class<? extends Formatter> clazz = formatter.getClass();
        Field argsField = clazz.getDeclaredField("args");
        argsField.setAccessible(true);
        return (Map<String, Supplier<String>>) argsField.get(formatter);
    }

    @Test
    void disallowsStrangeArgNames() throws NoSuchFieldException, IllegalAccessException {
        Formatter formatter = new Formatter();
        formatter.addArg(" ", "no")
                 .addArg("328905y )(* Y&r9 4q-e 0", "no")
                 .addArg("{bla}", () -> "no");

        assertEquals(0, this.makeArgsAccesable(formatter).size());
    }

    @Test
    void testFormatting() {
        Formatter formatter = new Formatter()
                .addArg("test", "success")
                .addArg("cat", "Zoey")
                .addArg("dynamic", () -> "really cool")
                .addArg("bla", () -> "By the nine devines, a {dragon}");

        assertEquals("This is a success", formatter.format("This is a {test}"));
        assertEquals("I love Zoey", formatter.format("I love {cat}"));
        assertEquals("This is really cool", formatter.format("This is {dynamic}"));
        assertEquals("By the nine devines, a {dragon}", formatter.format("{bla}"));
    }
}