package net.polarizedions.polarizedbot.autoresponders.impl;

import mocks.MockMessage;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.util.MessageUtil;
import net.polarizedions.polarizedbot.util.MessageUtilTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.obj.IMessage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoUnitConverterTest {
    private static AutoUnitConverter converter;

    @BeforeAll
    static void setup() throws NoSuchFieldException, IllegalAccessException {
        converter = new AutoUnitConverter();

        // Because otherwise the messages will never "send"
        MessageUtilTest.disableRatelimitHandling();
    }

    @BeforeAll
    static void resetMaxLen() throws NoSuchFieldException, IllegalAccessException {
        // Because MessageUtilTest#changeMaxLen changes it
        Class<MessageUtil> clazz = MessageUtil.class;
        Field maxLen = clazz.getDeclaredField("MAX_MESSAGE_LENGTH");
        maxLen.setAccessible(true);
        maxLen.set(null, IMessage.MAX_MESSAGE_LENGTH);
    }

    @Test
    void temperature() {
        MockMessage message = new MockMessage("It was nearly 30c the other day. At least it wasn't 30°F, or heck" +
                " even 30 K would be bad. -40C");

        converter.run(message);
        Bot.logger.debug("Running temp convection: {}", message.channel.sentMessages);
        assertEquals(1, message.channel.sentMessages.size());
        List<String> temps = Arrays.stream(message.channel.sentMessages.get(0).split("\n")).filter(s -> s.contains("->")).collect(Collectors.toList());
        Bot.logger.debug("Converted temps: {}", temps);
        assertTrue(temps.contains("30.0 °C -> 86 °F"));
        assertTrue(temps.contains("30.0 °F -> -1.11 °C"));
        assertTrue(temps.contains("30.0 K -> 303.15 °C"));
        assertTrue(temps.contains("-40.0 °C -> -40 °F"));
        assertEquals(4, temps.size());
    }

    @Test
    void length() {
        MockMessage message = new MockMessage("5'3\" 21.5\" 0.2 meters 8km 40 cm 1 foot 7 in");
        converter.run(message);
        Bot.logger.debug("Running length conversion: {}", message.channel.sentMessages);
        assertEquals(1, message.channel.sentMessages.size());
        List<String> lengths = Arrays.stream(message.channel.sentMessages.get(0).split("\n")).filter(s -> s.contains("->")).collect(Collectors.toList());
        Bot.logger.debug("Converted lengths: {}", lengths);
        assertTrue(lengths.contains("5.0 ft -> 1.52 m"));
        assertTrue(lengths.contains("3.0 in -> 7.62 cm"));
        assertTrue(lengths.contains("21.5 in -> 54.61 cm"));
        assertTrue(lengths.contains("0.2 m -> 0.66 ft"));
        assertTrue(lengths.contains("8.0 km -> 4.97 mi"));
        assertTrue(lengths.contains("40.0 cm -> 15.75 in"));
        assertTrue(lengths.contains("1.0 ft -> 0.3 m"));
        assertTrue(lengths.contains("7.0 in -> 17.78 cm"));
        assertEquals(8, lengths.size());
    }

    @Test
    void naughty() {
        String[] naughtyMessages = new String[] {"bl4ck", "20 mins"};

        for (String msgStr : naughtyMessages) {
            MockMessage message = new MockMessage(msgStr);
            converter.run(message);
            assertEquals(0, message.channel.sentMessages.size(), "Failed naughty word " + msgStr);
        }
    }

    @Test
    void common() {
        String[] messages = new String[] {
                "4c is cold",
                "Testing 4F capitalization",
                "Seperation 4 K okay",
                "And lowercase 4 f okay",
                "Test ending with 4c",
                "Test using 25 C in a sentance.",
                "With punctation:140C!",
        };

        for (String msg : messages) {
            MockMessage message = new MockMessage(msg);
            converter.run(message);
            assertEquals(1, message.channel.sentMessages.size(), "Failed common phrase: " + msg);
        }
    }
}