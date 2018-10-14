package net.polarizedions.polarizedbot.autoresponders.impl;

import net.polarizedions.polarizedbot.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MeasurementConverterTest {
    private static MeasurementConverter converter;

    @BeforeAll
    static void setup() {
        converter = new MeasurementConverter();
    }

    @Test()
    @Disabled("TODO")
    void run() {
        // TODO
        fail();
    }

    @Test
    void skipSpaces() {
        assertEquals(0, converter.skipSpaces("hello", 0)); // no space, beginning
        assertEquals(3, converter.skipSpaces("   hello", 0)); // space beginning
        assertEquals(5, converter.skipSpaces("   hello     how are you?", 5)); // no space, in word
        assertEquals(13, converter.skipSpaces("   hello     how are you?", 8)); // space, in sentence
    }

    @Test
    void readDouble() {
        assertEquals(new Pair<>(null, 0), converter.readDouble("hello", 0));
        assertEquals(new Pair<>(null, 4), converter.readDouble("12 hello", 4));
        assertEquals(new Pair<>(12.0, 2), converter.readDouble("12 hello", 0));
        assertEquals(new Pair<>(12.3, 4), converter.readDouble("12.3 hello", 0));
        assertEquals(new Pair<>(null, 0), converter.readDouble("hello        24 23", 0));
        assertEquals(new Pair<>(24.0, 15), converter.readDouble("hello        24 23", 5));
    }

    @Test
    void isDigit() {
        assertTrue(converter.isDigit('1'));
        assertTrue(converter.isDigit('.'));

        assertFalse(converter.isDigit('@'));
        assertFalse(converter.isDigit(' '));
    }

    @Test
    void readUnit() {
        assertEquals(new Pair<>("word", 4), converter.readUnit("word", 0));
        assertEquals(new Pair<>("thing", 10), converter.readUnit("word thing", 4));
        assertEquals(new Pair<>(null, 0), converter.readUnit("12", 0));
        assertEquals(new Pair<>("word", 17), converter.readUnit("             word   thing", 0));
        assertEquals(new Pair<>("thing", 25), converter.readUnit("             word   thing", 17));
    }

    @Test
    void isWordChar() {
        assertTrue(converter.isWordChar('a'));
        assertTrue(converter.isWordChar('S'));
        assertTrue(converter.isWordChar('r'));

        assertFalse(converter.isWordChar('#'));
        assertFalse(converter.isWordChar('4'));
        assertFalse(converter.isWordChar(' '));
    }

    @Test
    void isPunctuation() {
        assertTrue(converter.isPunctuation('.'));
        assertTrue(converter.isPunctuation('!'));
        assertTrue(converter.isPunctuation(','));
        assertTrue(converter.isPunctuation('('));
        assertTrue(converter.isPunctuation(')'));

        assertFalse(converter.isPunctuation('$'));
        assertFalse(converter.isPunctuation('^'));
        assertFalse(converter.isPunctuation('a'));
        assertFalse(converter.isPunctuation('A'));
        assertFalse(converter.isPunctuation('3'));
    }

    @Test
    void skipWord() {

        assertEquals(4, converter.skipWord("word", 0));
        assertEquals(4, converter.skipWord("word thing", 4));
        assertEquals(0, converter.skipWord("12", 0));
    }
}