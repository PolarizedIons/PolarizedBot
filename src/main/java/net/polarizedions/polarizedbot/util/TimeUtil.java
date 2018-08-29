package net.polarizedions.polarizedbot.util;

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


    public static String formatDuration(Localizer localizer, Duration duration) {
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

            String key = formatKeys[i];
            if (localizer.doesKeyExist(key + "." + time[i])) {
                key = key + "." + time[i];
            }

            formatted.append(localizer.localize(key, time[i])).append(" ");
        }

        if (formatted.length() == 0) { // WAT o.o
            String key = formatKeys[4];
            if (localizer.doesKeyExist(key + ".0")) {
                key = key + ".0";
            }

            formatted.append(localizer.localize(key, 0));
        }

        return formatted.toString().trim();
    }
}
