package net.polarizedions.polarizedbot.autoresponders.impl;

import mocks.MockChannel;
import mocks.MockMessage;
import mocks.SetupMocks;
import net.polarizedions.polarizedbot.util.Localizer;
import net.polarizedions.polarizedbot.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementConverterTest {
    private static MeasurementConverter converter;

    @BeforeAll
    static void setup() throws NoSuchFieldException, IllegalAccessException {
        converter = new MeasurementConverter();
        SetupMocks.setupRatelimitMock();
        Localizer.init();
    }

    @Test()
    void findUnits() {
        MockMessage mockMsg = new MockMessage("31 cm to your right, 5 inches to your left, and 4000 ft down, and then 5 inches to your right."); // It's 3'14\" long."); // TODO: THIS COMMENTED OUT ONE
        converter.run(mockMsg);
        assertEquals(1, mockMsg.channel.sentMessages.size());
        List<String> messageLines = Arrays.stream(mockMsg.channel.sentMessages.get(0).split("\n")).filter(line -> ! line.equals("```")).collect(Collectors.toList());
        System.out.println(messageLines);
        assertEquals(3, messageLines.size());
        assertTrue(messageLines.contains("31 centimeters = 1.02 foot"));
        assertTrue(messageLines.contains("5 inches = 12.7 centimeters"));
        assertTrue(messageLines.contains("4000 feet = 1219.14 meters"));
//        assertTrue(messageLines.contains("3 feet = 91,44 centimeters"));
//        assertTrue(messageLines.contains("14 inches = 35.56 centimeters"));
    }

    @Test
    void naughtyStrings() {
        MockChannel mockChannel = new MockChannel();
        converter.run(new MockMessage(0, mockChannel, "We are 4 in here."));
        converter.run(new MockMessage(0, mockChannel, "look whats up on the bridge now https://scontent-yyz1-1.xx.fbcdn.net/v/t1.0-9/43880116_2136757986643099_8466591188617527296_n.jpg?_nc_cat=108&oh=dbd6c9271c361c4a6854c9252b73e1a7&oe=5C51E275"));


        assertEquals(0, mockChannel.sentMessages.size(), "Matched naughty strings!: " + mockChannel.sentMessages);
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
        assertTrue(converter.isPunctuation('\''));
        assertTrue(converter.isPunctuation('"'));

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