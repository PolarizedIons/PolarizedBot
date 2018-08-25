package net.polarizedions.polarizedbot;

import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.Test;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import static org.junit.jupiter.api.Assertions.*;

class BotTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test()
    void createClientWithoutToken() {
        environmentVariables.clear("BOT_TOKEN");
        assertThrows(RuntimeException.class, () -> new Bot().createClient(false));
        assertThrows(RuntimeException.class, () -> new Bot().createClient(true));
    }

    @Test
    void createClientWithoutLogin() {
        environmentVariables.set("BOT_TOKEN", "123");
        IDiscordClient client = new Bot().createClient(false);
        assertNotNull(client);
        assertFalse(client.isLoggedIn());
    }
}