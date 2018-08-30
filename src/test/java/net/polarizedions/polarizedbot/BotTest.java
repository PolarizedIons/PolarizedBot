package net.polarizedions.polarizedbot;

import org.junit.jupiter.api.Test;
import sx.blah.discord.api.IDiscordClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BotTest {
    @Test
    void createClientWithoutLogin() {
        IDiscordClient client = new Bot().createClient(false);
        assertNotNull(client);
        assertFalse(client.isLoggedIn());
    }
}