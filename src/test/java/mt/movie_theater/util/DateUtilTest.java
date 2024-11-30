package mt.movie_theater.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateUtilTest {

    @DisplayName("yyy.MM.dd (EEE) 형식으로 변환한다.")
    @Test
    void formatToStartDate() {
        //given
        LocalDateTime dateTime = LocalDateTime.of(2024, 11, 30, 10, 0);

        //when
        String formatDate = DateUtil.formatToStartDate(dateTime);

        //then
        assertThat(formatDate).isEqualTo("2024.11.30 (토)");
    }

    @DisplayName("HH:mm 형식으로 변환한다.")
    @Test
    void formatToHourAndMinute() {
        //given
        LocalDateTime dateTime = LocalDateTime.of(2024, 11, 30, 10, 0);

        //when
        String formatDate = DateUtil.formatToHourAndMinute(dateTime);

        //then
        assertThat(formatDate).isEqualTo("10:00");
    }

    @DisplayName("yyyy.MM.dd (EEE) HH:mm 형식으로 변환한다.")
    @Test
    void formatToDateAndHourAndMinute() {
        //given
        LocalDateTime dateTime = LocalDateTime.of(2024, 11, 30, 10, 0);

        //when
        String formatDate = DateUtil.formatToDateAndHourAndMinute(dateTime);

        //then
        assertThat(formatDate).isEqualTo("2024.11.30 (토) 10:00");
    }
}