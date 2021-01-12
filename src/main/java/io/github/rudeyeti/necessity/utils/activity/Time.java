package io.github.rudeyeti.necessity.utils.activity;

import org.apache.commons.lang.math.NumberUtils;

import java.time.Duration;

public class Time {
    public static String get(String time) {
        // The time must have a unit at the end.
        if (!NumberUtils.isDigits(time)) {
            String regex = "(s(econds?)?|m(inutes?)?|h(ours?)?|d(ays?)?|w(eeks?)?)";

            if (time.matches("\\d+" + regex)) {
                int number = Integer.parseInt(time.replaceAll(regex, ""));

                if (number > 0) {
                    // Just receiving the unit and not the rest of the string.
                    String unit = String.valueOf(time.replaceAll("[^smhdw]", "").charAt(0));

                    switch (unit) {
                        case "s":
                            return String.valueOf(number);
                        case "m":
                            return String.valueOf(Duration.ofMinutes(1).getSeconds() * number);
                        case "h":
                            return String.valueOf(Duration.ofHours(1).getSeconds() * number);
                        case "d":
                            return String.valueOf(Duration.ofDays(1).getSeconds() * number);
                        case "w":
                            return String.valueOf(Duration.ofDays(1).multipliedBy(7).getSeconds() * number);
                    }
                } else {
                    return "Usage: The specified time must be at least 1.";
                }
            } else {
                return "Usage: The specified time must be a number with a unit at the end.";
            }
        } else {
            return "Usage: The specified time must have a unit at the end.";
        }
        return null;
    }
}
