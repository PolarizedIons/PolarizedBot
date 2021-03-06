package net.polarizedions.polarizedbot.util;

import mocks.MockChannel;
import mocks.MockMessage;
import mocks.SetupMocks;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageUtilTest {

    @AfterAll
    static void tearDown() throws NoSuchFieldException, IllegalAccessException {
        SetupMocks.resetMaxMessageLength();
        SetupMocks.resetLocalization();
    }

    @BeforeAll
    static void setp() throws NoSuchFieldException, IllegalAccessException {
        // Because the ratelimiting handling wont work without the bot running
        SetupMocks.setupRatelimitMock();

        // For the sake of testing, reduce the max length of messages
        SetupMocks.setupMaxMessageLengthMock();
    }

    @Test
    void sendAutosplit() {
        MockChannel channel = new MockChannel();
        MessageUtil.sendAutosplit(channel, "short message\n");
        assertEquals(1, channel.sentMessages.size());
        assertEquals("short message\n", channel.getSentContent().get(0));

        channel.sentMessages.clear();

        MessageUtil.sendAutosplit(channel, "Stop right there criminal scum!\n" +
                "Let me guess... someone stole your sweetroll.\n" +
                "No lollygaggin'.\n" +
                "What is it? Dragons?\n" +
                "Wait... I know you.\n" +
                "I used to be an adventurer like you. Then I took an arrow in the knee...");

        assertEquals(4, channel.sentMessages.size());

        assertEquals("Stop right there criminal scum!\n", channel.getSentContent().get(0));
        assertEquals("Let me guess... someone stole your sweetroll.\n" +
                "No lollygaggin'.\n", channel.getSentContent().get(1));
        assertEquals("What is it? Dragons?\n" +
                "Wait... I know you.\n", channel.getSentContent().get(2));
        assertEquals("I used to be an adventurer like you. Then I took an arrow in the knee...\n", channel.getSentContent().get(3));
    }

    @Test
    void sendAutosplitWithPreAndSuffixes() {
        MockChannel channel = new MockChannel();
        MessageUtil.sendAutosplit(channel, "```\n" +
                "Raindrops on roses\n" +
                "And whiskers on kittens\n" +
                "Bright copper kettles and warm woolen mittens\n" +
                "Brown paper packages tied up with strings\n" +
                "These are a few of my favorite things\n\n" +
                "Cream-colored ponies and crisp apple strudels\n" +
                "Doorbells and sleigh bells\n" +
                "And schnitzel with noodles\n" +
                "Wild geese that fly with the moon on their wings\n" +
                "These are a few of my favorite things\n" +
                "```", "```", "```");

        assertEquals(8, channel.sentMessages.size());
        for (String msg : channel.getSentContent()) {
            msg = msg.trim();
            assertTrue(msg.startsWith("```"));
            assertTrue(msg.endsWith("```"));
        }
    }

    @Test
    void reply() throws NoSuchFieldException, IllegalAccessException {
        // Because reply localizes, so we need to load our testlanguage
        SetupMocks.setupLocalization();

        MockMessage message = new MockMessage();
        MessageUtil.reply(message, "test");
        MessageUtil.reply(message, "hello", "Polar");

        assertEquals(3, message.channel.sentMessages.size());
        assertEquals("success", message.channel.getSentContent().get(1));
        assertEquals("Hello Polar", message.channel.getSentContent().get(2));
    }
}