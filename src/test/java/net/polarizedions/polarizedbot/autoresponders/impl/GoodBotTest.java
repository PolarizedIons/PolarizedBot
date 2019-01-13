package net.polarizedions.polarizedbot.autoresponders.impl;

import mocks.MockChannel;
import mocks.MockMessage;
import mocks.MockUser;
import mocks.SetupMocks;
import net.polarizedions.polarizedbot.Bot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sx.blah.discord.handle.obj.IUser;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoodBotTest {

    private static GoodBot goodBot;

    @BeforeAll
    static void setup() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        SetupMocks.botClient();
        SetupMocks.setupRatelimitMock();
        goodBot = new GoodBot();
    }

    @Test
    void replyToPing() {
        IUser us = Bot.instance.getClient().getOurUser();
        MockChannel channel = new MockChannel();
        MockMessage msg = new MockMessage(123, channel, us.mention() + " Good bot");
        goodBot.run(msg);

        assertEquals(2, channel.sentMessages.size());
        assertEquals("❤", channel.getSentContent().get(1));
    }

    @Test
    void replyToThank() {
        MockChannel channel = new MockChannel();
        new MockMessage(987546, channel, "blablabla", new MockUser(SetupMocks.BOT_USER_ID)).time = Instant.now().minusMillis(5000);

        MockMessage msg = new MockMessage(123, channel, "Good bot!");
        msg.time = Instant.now().minusMillis(1000L);
        goodBot.run(msg);

        assertEquals(3, msg.channel.sentMessages.size());
        assertEquals("❤", msg.channel.getSentContent().get(2));
    }
}