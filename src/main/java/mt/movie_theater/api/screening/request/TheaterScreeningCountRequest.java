package mt.movie_theater.api.screening.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mt.movie_theater.domain.theater.Region;
import org.springframework.format.annotation.DateTimeFormat;

@NoArgsConstructor
@Getter
@Setter
public class TheaterScreeningCountRequest {

    @NotNull(message = "날짜는 필수 입력값입니다.")
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private LocalDate date;

    private Long movieId;

    @NotNull(message = "지역은 필수 입력값입니다.")
    private Region region;
}
