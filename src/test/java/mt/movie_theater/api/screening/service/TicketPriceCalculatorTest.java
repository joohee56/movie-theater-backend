package mt.movie_theater.api.screening.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TicketPriceCalculatorTest {

    @DisplayName("조조 시간에 해당하는 경우, 80% 할인된 금액을 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "2024-10-31T06:00:00, 8000",
            "2024-10-31T09:00:00, 8000",
            "2024-10-31T10:00:00, 8000",
            "2024-10-31T05:59:59, 10000",
            "2024-10-31T10:00:01, 10000"
    })
    void calculateFinalPriceWithMorningDiscount(String startTimeStr, int expected) {
        //given
        LocalDateTime startDate = LocalDateTime.parse(startTimeStr);
        int standardPrice = 10000;
        int hallTypedModifier = 0;

        //when
        int finalPrice = TicketPriceCalculator.calculateFinalPrice(standardPrice, hallTypedModifier, startDate);

        //then
        assertThat(finalPrice).isEqualTo(expected);
    }

    @DisplayName("심야 시간에 해당하는 경우, 90% 할인된 금액을 반환한다.")
    @ParameterizedTest
    @CsvSource({
            "2024-10-31T22:00:00, 9000",
            "2024-10-31T23:00:00, 9000",
            "2024-11-01T01:00:00, 9000",
            "2024-11-01T02:00:00, 9000",
            "2024-10-31T21:59:59, 10000",
            "2024-11-01T02:00:01, 10000"
    })
    void calculateFinalPriceWithNightDiscount(String startTimeStr, int expected) {
        //given
        LocalDateTime startDate = LocalDateTime.parse(startTimeStr);
        int standardPrice = 10000;
        int hallTypedModifier = 0;

        //when
        int finalPrice = TicketPriceCalculator.calculateFinalPrice(standardPrice, hallTypedModifier, startDate);

        //then
        assertThat(finalPrice).isEqualTo(expected);
    }

    @DisplayName("최종 티켓 금액을 계산한다.")
    @ParameterizedTest
    @CsvSource({
            "2024-10-31T06:00:00, 10400",
            "2024-10-31T10:00:00, 10400",
            "2024-10-31T22:00:00, 11700",
            "2024-11-01T02:00:00, 11700",
            "2024-10-31T15:00:00, 13000"
    })
    void calculateFinalPrice(String startTimeStr, int expected) {
        //given
        LocalDateTime startDate = LocalDateTime.parse(startTimeStr);
        int standardPrice = 10000;
        int hallTypedModifier = 3000;

        //when
        int finalPrice = TicketPriceCalculator.calculateFinalPrice(standardPrice, hallTypedModifier, startDate);

        //then
        assertThat(finalPrice).isEqualTo(expected);
    }

}