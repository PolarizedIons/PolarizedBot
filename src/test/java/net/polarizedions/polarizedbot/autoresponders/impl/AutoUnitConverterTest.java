package net.polarizedions.polarizedbot.autoresponders.impl;

import mocks.MockMessage;
import net.polarizedions.polarizedbot.util.MessageUtilTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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

    @Test
    void temperature() {
        MockMessage message = new MockMessage("It was nearly 30c the other day. At least it wasn't 30°F, or heck" +
                " even 30 K would be bad.");

        converter.run(message);
        assertEquals(1, message.channel.sentMessages.size());
        List<String> temps = Arrays.asList(message.channel.sentMessages.get(0).split("\n"));
        assertTrue(temps.contains("30.0 °C -> 86 °F"));
        assertTrue(temps.contains("30.0 °F -> -1.11 °C"));
        assertTrue(temps.contains("30.0 K -> 303.15 °C"));
    }

    @Test
    @Disabled
    void length() {
        MockMessage message = new MockMessage("5'3\" 21.5\" 0.2 meters 8km 40 cm 1 foot 7 in");
        converter.run(message);
        assertEquals(1, message.channel.sentMessages.size());
        List<String> lengths = Arrays.asList(message.channel.sentMessages.get(0).split("\n"));
        assertTrue(lengths.contains("5.0 ft -> 1.52 m"));
        assertTrue(lengths.contains("21.5 in -> 54.61 cm"));
        assertTrue(lengths.contains("3.0 in -> 7.62 cm"));
        assertTrue(lengths.contains("2.0 m -> 6.56 ft"));
        assertTrue(lengths.contains("8.0 km -> 4.97 mi"));
        assertTrue(lengths.contains("40.0 °C -> 104 °F"));
        assertTrue(lengths.contains("1.0 ft -> 0.3 m"));
        assertTrue(lengths.contains("7.0 in -> 17.78 cm"));
    }

    @Test
    @Disabled
    void naughty() {
        String[] naughtyMessages = new String[] {"bl4ck", "20 mins"};

        for (String msgStr : naughtyMessages) {
            MockMessage message = new MockMessage(msgStr);
            converter.run(message);
            assertEquals(0, message.channel.sentMessages.size(), "Failed naughty word " + msgStr);
        }
    }
}