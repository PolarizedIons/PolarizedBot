package net.polarizedions.polarizedbot.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class LocalizerTest {
    @BeforeAll
    static void mockLang() throws NoSuchFieldException, IllegalAccessException {
        Class<Localizer> clazz = Localizer.class;
        Field field = clazz.getField("AVAILABLE_LANGS");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, new String[] {"en", "testlang"});

        Localizer.init();
    }

    @Test
    @SuppressWarnings("unchecked")
    void loadedData() throws NoSuchFieldException, IllegalAccessException {
        Field langDataField = Localizer.class.getDeclaredField("langData");
        langDataField.setAccessible(true);
        Map<String, Properties> langData = (Map<String, Properties>) langDataField.get(null);

        assertNotNull(langData);

        Localizer.setCurrentLang("en");
        assertNotNull(langData.get("en"));
        assertNotNull(langData.get("testlang"));
    }

    @Test
    void supports() {
        assertFalse(Localizer.supports("foobar"));
        assertTrue(Localizer.supports("testlang"));
        assertTrue(Localizer.supports("TeStLaNg"));
    }

    static String getCurrentLang() throws NoSuchFieldException, IllegalAccessException {
        Field field = Localizer.class.getDeclaredField("currentLang");
        field.setAccessible(true);
        return (String)field.get(null);
    }

    @Test
    void changeLang() throws NoSuchFieldException, IllegalAccessException {
        Localizer.setCurrentLang("en");
        assertEquals("en", getCurrentLang());

        // Doesn't set unknown lang
        Localizer.setCurrentLang("foobar");
        assertEquals("en", getCurrentLang());

        Localizer.setCurrentLang("testlang");
        assertEquals("testlang", getCurrentLang());
    }

    @Test
    void formating() {
        Localizer.setCurrentLang("testlang");
        assertEquals("success", Localizer.localize("test"));
        assertEquals("Hello World!", Localizer.localize("hello", "World!"));
        assertEquals("hi polar", Localizer.localize("test.deep.keys", "polar"));
        assertEquals("2 1", Localizer.localize("order", "1", 2));
    }
}