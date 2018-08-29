package net.polarizedions.polarizedbot.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LocalizerTest {
    @BeforeAll
    static void mockLang() throws NoSuchFieldException, IllegalAccessException {
        Class<Localizer> clazz = Localizer.class;
        Field field = clazz.getField("AVAILABLE_LANGUAGES");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        List<String> newSupports = new ArrayList<>();
        Collections.addAll(newSupports, (String[])field.get(null));
        newSupports.add("testlang");
        field.set(null, newSupports.toArray(new String[0]));

        Localizer.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    void loadedData() throws NoSuchFieldException, IllegalAccessException {
        Field langDataField = Localizer.class.getDeclaredField("langData");
        langDataField.setAccessible(true);
        Map<String, Properties> langData = (Map<String, Properties>) langDataField.get(null);

        assertNotNull(langData);

        assertNotNull(langData.get("en"));
        assertNotNull(langData.get("testlang"));
    }

    @Test
    void supports() {
        assertFalse(Localizer.supports("foobar"));
        assertTrue(Localizer.supports("testlang"));
        assertTrue(Localizer.supports("TeStLaNg"));
    }

    @Test
    void changeLang() throws NoSuchFieldException, IllegalAccessException {
        Localizer loc = new Localizer("en");
        assertEquals("en", loc.getCurrentLang());

        // Doesn't set unknown lang
        loc.setCurrentLang("foobar");
        assertEquals("en", loc.getCurrentLang());

        loc.setCurrentLang("TeStLaNg");
        assertEquals("testlang", loc.getCurrentLang());
    }

    @Test
    void formating() {
        Localizer loc = new Localizer("testlang");
        assertEquals("success", loc.localize("test"));
        assertEquals("Hello World!", loc.localize("hello", "World!"));
        assertEquals("hi polar", loc.localize("test.deep.keys", "polar"));
        assertEquals("2 1", loc.localize("order", "1", 2));
    }
}