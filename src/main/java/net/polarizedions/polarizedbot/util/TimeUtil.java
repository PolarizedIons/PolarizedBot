package net.polarizedions.polarizedbot.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    private static String[] formatKeys = new String[] {
            "format.week",
            "format.day",
            "format.hour",
            "format.minute",
            "format.second",
    };


    @NotNull
    public static String formatDuration(Localizer localizer, @NotNull Duration duration) {
        long[] time = new long[5];
        time[0] = duration.toDays() / 7;
        time[1] = duration.toDays() % 7;
        time[2] = duration.toHours() % TimeUnit.DAYS.toHours(1);
        time[3] = duration.toMinutes() % TimeUnit.HOURS.toMinutes(1);
        time[4] = duration.getSeconds() % TimeUnit.MINUTES.toSeconds(1);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < time.length; i++) {
            if (time[i] == 0 && formatted.length() == 0) {
                continue;
            }

            formatted.append(localizer.localizeNumber(formatKeys[i], (int)time[i], time[i])).append(" ");
        }

        return formatted.toString().trim();
    }
}
