package mt.movie_theater.api.screening.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class TicketPriceCalculator {
    public static final LocalTime MORNING_DISCOUNT_START_TIME = LocalTime.of(6, 0);
    public static final LocalTime MORNING_DISCOUNT_END_TIME = LocalTime.of(10, 0);

    public static final LocalTime NIGHT_DISCOUNT_START_TIME = LocalTime.of(22, 0);
    public static final LocalTime NIGHT_DISCOUNT_END_TIME = LocalTime.of(2, 0); // 다음 날 새벽 2시까지

    public static int calculateFinalPrice(int standardPrice, int hallTypeModifier, LocalDateTime startDate) {
        standardPrice += hallTypeModifier;
        if (isMorningDiscount(startDate.toLocalTime())) {
            standardPrice *= 0.8;
        } else if (isNightDiscount(startDate.toLocalTime())) {
            standardPrice *= 0.9;
        }
        return standardPrice;
    }

    private static boolean isMorningDiscount(LocalTime startDate) {
        return startDate.equals(MORNING_DISCOUNT_START_TIME) || (startDate.isAfter(MORNING_DISCOUNT_START_TIME) && startDate.isBefore(MORNING_DISCOUNT_END_TIME)) || startDate.equals(MORNING_DISCOUNT_END_TIME);
    }

    private static boolean isNightDiscount(LocalTime startDate) {
        return startDate.equals(NIGHT_DISCOUNT_START_TIME) || startDate.isAfter(NIGHT_DISCOUNT_START_TIME) || startDate.isBefore(NIGHT_DISCOUNT_END_TIME) || startDate.equals(NIGHT_DISCOUNT_END_TIME);
    }
}
