package net.polarizedions.polarizedbot.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilTest {

    @BeforeAll
    static void setup() throws NoSuchFieldException, IllegalAccessException {
//        SetupMocks.resetLocalization();
        // TODO
    }

    @Test
    void formatDuration() {
        Duration oneSec = Duration.ofSeconds(1);
        Duration fiveSec = Duration.ofSeconds(5);
        Duration allOnes = Duration.ofSeconds(1)
                .plusMinutes(1)
                .plusHours(1)
                .plusDays(8);
        Duration onlyHours = Duration.ofSeconds(16)
                .plusMinutes(54)
                .plusHours(22);
        Duration fullRandom = Duration.ofSeconds(5)
                .plusMinutes(1)
                .plusHours(18)
                .plusDays(13);

        Localizer loc = new Localizer("en");

        assertEquals("1 second", TimeUtil.formatDuration(loc, oneSec));
        assertEquals("5 seconds", TimeUtil.formatDuration(loc, fiveSec));
        assertEquals("1 week 1 day 1 hour 1 minute 1 second", TimeUtil.formatDuration(loc, allOnes));
        assertEquals("22 hours 54 minutes 16 seconds", TimeUtil.formatDuration(loc, onlyHours));
        assertEquals("1 week 6 days 18 hours 1 minute 5 seconds", TimeUtil.formatDuration(loc, fullRandom));
    }
}