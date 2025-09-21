package io.github.maths.shiny.utils;

import org.apache.commons.lang.time.*;
import java.util.concurrent.*;

public final class JavaUtil
{
    public static Integer tryParseInt(final String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static String formatDurationInt(final int input) {
        return DurationFormatUtils.formatDurationWords(input * 1000L, true, true);
    }
    
    public static String formatDurationLongMiliseconds(final long input) {
        final long millis = input % 1000L;
        final int seconds = (int)(input / 1000L) % 60;
        return seconds + "." + millis + " seconds";
    }
    
    public static String formatDurationLong(final long input) {
        return DurationFormatUtils.formatDurationWords(input, true, true);
    }
    
    public static String formatLongMin(final long time) {
        final long totalSecs = time / 1000L;
        return String.format("%02d:%02d", totalSecs / 60L, totalSecs % 60L);
    }
    
    public static String formatLongHour(final long time) {
        final long totalSecs = time / 1000L;
        final long seconds = totalSecs % 60L;
        final long minutes = totalSecs % 3600L / 60L;
        final long hours = totalSecs / 3600L;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public static long formatLong(final String input) {
        if (input == null || input.isEmpty()) {
            return -1L;
        }
        long result = 0L;
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
            }
            else {
                final String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convertLong(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }
    
    private static long convertLong(final int value, final char unit) {
        switch (unit) {
            case 'y': {
                return value * TimeUnit.DAYS.toMillis(365L);
            }
            case 'M': {
                return value * TimeUnit.DAYS.toMillis(30L);
            }
            case 'd': {
                return value * TimeUnit.DAYS.toMillis(1L);
            }
            case 'h': {
                return value * TimeUnit.HOURS.toMillis(1L);
            }
            case 'm': {
                return value * TimeUnit.MINUTES.toMillis(1L);
            }
            case 's': {
                return value * TimeUnit.SECONDS.toMillis(1L);
            }
            default: {
                return -1L;
            }
        }
    }
    
    public static int formatInt(final String input) {
        if (input == null || input.isEmpty()) {
            return -1;
        }
        int result = 0;
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
            }
            else {
                final String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convertInt(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }
    
    private static int convertInt(final int value, final char unit) {
        switch (unit) {
            case 'd': {
                return value * 60 * 60 * 24;
            }
            case 'h': {
                return value * 60 * 60;
            }
            case 'm': {
                return value * 60;
            }
            case 's': {
                return value;
            }
            default: {
                return -1;
            }
        }
    }
    
    public static int randomExcluding(final int start, final int end, final int... exclude) {
        int random = start + ThreadLocalRandom.current().nextInt(end - start + 1 - exclude.length);
        for (final int ex : exclude) {
            if (random < ex) {
                break;
            }
            ++random;
        }
        return random;
    }
    
    public static boolean getChance(final double minimalChance) {
        return ThreadLocalRandom.current().nextDouble(99.0) + 1.0 >= 100.0 - minimalChance;
    }
    
    private JavaUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
