package mt.movie_theater.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class DateUtil {
    public static String formatToStartDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (EEE)", Locale.KOREA);
        return dateTime.format(formatter);
    }

    public static String formatToHourAndMinute(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    public static String formatToDateAndHourAndMinute(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (EEE) HH:mm", Locale.KOREA);
        return dateTime.format(formatter);
    }
}
