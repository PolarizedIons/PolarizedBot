package net.polarizedions.polarizedbot.autoresponders.impl;

import mocks.MockMessage;
import mocks.SetupMocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempConverterTest {
    @BeforeAll
    static void setup() throws Exception {
        SetupMocks.setupRatelimitMock();
        SetupMocks.resetLocalization();
    }

    @Test
    void noConversion() {
        TempConverter converter = new TempConverter();

        MockMessage msg = new MockMessage("What is better - to be born good, or to overcome your evil nature through great effort?");
        converter.run(msg);
        assertEquals(1, msg.channel.sentMessages.size());
    }

    @Test
    void simpleConversion() {
        TempConverter converter = new TempConverter();

        MockMessage msg = new MockMessage("34 C weather I hate. 80f is okay. Should it be 15.5°c though," +
                " that's nice. -40 °F is pretty neat, and -8000C is impossible");
        converter.run(msg);
        assertEquals(2, msg.channel.sentMessages.size());

        List<String> lines = Arrays.stream(msg.channel.getSentContent().get(1).split("\n")).filter(l -> ! l.equals("```")).collect(Collectors.toList());
        System.out.println(lines);
        assertEquals(5, lines.size());
        assertTrue(lines.contains("34 °C -> 93.2 °F"));
        assertTrue(lines.contains("80 °F -> 26.67 °C"));
        assertTrue(lines.contains("15.5 °C -> 59.9 °F"));
        assertTrue(lines.contains("-40 °F -> -40 °C"));
        assertTrue(lines.contains("-8000 °C -> impossible"));
    }

    @Test
    void naughtyPhrases() {
        String[] phrases = new String[] {
                "Punctiation.-34C",
                "$325c4-24f54.3f",
                "12 centimeters",
        };

        TempConverter converter = new TempConverter();

        for (String p : phrases) {
            MockMessage msg = new MockMessage(p);
            converter.run(msg);
            assertEquals(1, msg.channel.sentMessages.size(), "Matched naughty phrase: " + p);
        }
    }
}