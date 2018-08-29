package net.polarizedions.polarizedbot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRankTest {

    @Test
    void sensibleValues() {
        assertTrue(UserRank.BOT_OWNER.rank > UserRank.GUILD_ADMIN.rank);
        assertTrue(UserRank.GUILD_ADMIN.rank > UserRank.DEFAULT.rank);
    }

    @Test
    void getByName() {
        assertEquals(UserRank.DEFAULT, UserRank.getByName("none"));
        assertEquals(UserRank.GUILD_ADMIN, UserRank.getByName("admin"));
    }

    @Test
    void getNames() {
        assertTrue(UserRank.getNames().contains("admin"));
    }
}