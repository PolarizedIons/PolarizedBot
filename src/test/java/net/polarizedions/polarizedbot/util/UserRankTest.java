package net.polarizedions.polarizedbot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRankTest {

    @Test
    void sensibleValues() {
        assertTrue(UserRank.GLOBAL_ADMIN.rank > UserRank.LOCAL_ADMIN.rank);
        assertTrue(UserRank.LOCAL_ADMIN.rank > UserRank.DEFAULT.rank);
    }
}