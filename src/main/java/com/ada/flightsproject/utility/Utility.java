package com.ada.flightsproject.utility;

import java.util.Map;

public class Utility {

    private static final Map<String, Integer> DAY_TO_INDEX = Map.ofEntries(
            Map.entry("MONDAY", 0),
            Map.entry("TUESDAY", 1),
            Map.entry("WEDNESDAY", 2),
            Map.entry("THURSDAY", 3),
            Map.entry("FRIDAY", 4),
            Map.entry("SATURDAY", 5),
            Map.entry("SUNDAY", 6),

            // Allow short forms too
            Map.entry("MON", 0),
            Map.entry("TUE", 1),
            Map.entry("WED", 2),
            Map.entry("THU", 3),
            Map.entry("FRI", 4),
            Map.entry("SAT", 5),
            Map.entry("SUN", 6)
    );

    private static final int MINUTES_IN_DAY = 24 * 60;
    private static final int MINUTES_IN_WEEK = 7 * MINUTES_IN_DAY;

    /**
     * Converts (day + HH:mm + duration) into unified weekly minutes.
     *
     * @param dayName   e.g. "Monday", "Tue", "SUN"
     * @param timeHHMM  e.g. "13:45"
     * @param duration  flight duration in minutes
     * @return int[]{ departureWeekMinute, arrivalWeekMinute }
     */
    public static int[] computeDepartureArrivalMinutes(String dayName, String timeHHMM, int duration) {
        int dayIndex = parseDay(dayName);
        int departureMinutesInDay = parseTime(timeHHMM);

        int departureWeekMinute = dayIndex * MINUTES_IN_DAY + departureMinutesInDay;
        int arrivalWeekMinute = departureWeekMinute + duration;

        // If arrival spills past the end of the week, wrap naturally by exceeding 10080.
        // No modulo here — we WANT extended minutes because Dijkstra works with absolute times.
        return new int[]{ departureWeekMinute, arrivalWeekMinute };
    }

    private static int parseDay(String dayName) {
        String key = dayName.trim().toUpperCase();
        if (!DAY_TO_INDEX.containsKey(key)) {
            throw new IllegalArgumentException("Invalid day name: " + dayName);
        }
        return DAY_TO_INDEX.get(key);
    }

    private static int parseTime(String hhmm) {
        String[] parts = hhmm.trim().split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid time format: " + hhmm);

        int hh = Integer.parseInt(parts[0]);
        int mm = Integer.parseInt(parts[1]);
        return hh * 60 + mm;
    }

    private static final String[] INDEX_TO_DAY = {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    /**
     *
     * @param weekMinute
     * @return
     */
    public static String[] minutesToDayAndTime(int weekMinute) {
        // Normalize into the 0–10079 range to get the weekday and time-of-day
        int normalized = ((weekMinute % MINUTES_IN_WEEK) + MINUTES_IN_WEEK) % MINUTES_IN_WEEK;

        int dayIndex = normalized / MINUTES_IN_DAY;
        int minutesInDay = normalized % MINUTES_IN_DAY;

        String dayName = INDEX_TO_DAY[dayIndex];
        String timeHHMM = formatHHMM(minutesInDay);

        return new String[]{ dayName, timeHHMM };
    }

    private static String formatHHMM(int minutes) {
        int hh = minutes / 60;
        int mm = minutes % 60;
        return String.format("%02d:%02d", hh, mm);
    }

}

