package net.polarizedions.polarizedbot.util;

import mocks.SetupMocks;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizerTest {
    @BeforeAll
    static void mockLang() throws NoSuchFieldException, IllegalAccessException {
        SetupMocks.setupLocalization();
    }

    @AfterAll
    static void resetLang() throws NoSuchFieldException, IllegalAccessException {
        SetupMocks.resetLocalization();
    }

    @Test
    @SuppressWarnings("unchecked")
    void loadedData() throws NoSuchFieldException, IllegalAccessException {
        Field langDataField = Localizer.class.getDeclaredField("langData");
        langDataField.setAccessible(true);
        Map<String, Properties> langData = (Map<String, Properties>)langDataField.get(null);

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
    void changeLang() {
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

    @Test
    void numberFormatting() {
        Localizer loc = new Localizer("testlang");
        assertEquals("1 meow", loc.localizeNumber("numberformat.test", 1));
        assertEquals("-6 meow", loc.localizeNumber("numberformat.test", -6));
        assertEquals("0 meow", loc.localizeNumber("numberformat.test", 0));

        assertEquals("1", loc.localizeNumber("numberformat.bla", 1));
        assertEquals("two", loc.localizeNumber("numberformat.bla", 2));
        assertEquals("minus six", loc.localizeNumber("numberformat.bla", -6));
        assertEquals("0", loc.localizeNumber("numberformat.bla", 0));

        assertEquals("bla", loc.localizeNumber("numberformat.bla", 0, "bla"));
        assertEquals("minus six", loc.localizeNumber("numberformat.bla", -6, "zero"));

        assertEquals("1", loc.localizeNumber("numberformat.bla", 1));
        assertEquals("1", loc.localizeNumber("numberformat.bla", 0.5));
        assertEquals("two", loc.localizeNumber("numberformat.bla", 1.1));
        assertEquals("two", loc.localizeNumber("numberformat.bla", 2));
    }
}