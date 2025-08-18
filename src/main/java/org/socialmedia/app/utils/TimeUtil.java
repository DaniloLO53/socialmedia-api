package org.socialmedia.app.utils;

import java.time.Duration;
import java.time.OffsetDateTime;

public class TimeUtil {
    public static long getDaysBetweenRoundedUp(OffsetDateTime pastDateTime) {
        OffsetDateTime now = OffsetDateTime.now();

        Duration duration = Duration.between(pastDateTime, now);

        if (duration.isNegative() || duration.isZero()) {
            return 0;
        }

        double daysFractional = (double) duration.toSeconds() / 86400.0;
        return (long) Math.ceil(daysFractional);
    }
}
