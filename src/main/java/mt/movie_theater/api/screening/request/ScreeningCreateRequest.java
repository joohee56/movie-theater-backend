package mt.movie_theater.api.screening.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScreeningCreateRequest {

    @NotNull(message = "영화 ID는 필수 입력값입니다.")
    private Long movieId;

    @NotNull(message = "상영관 ID는 필수 입력값입니다.")
    private Long hallId;

    @NotNull(message = "상영 시작 시간은 필수 입력값입니다.")
    private List<LocalDateTime> startTimes;

    @Builder
    public ScreeningCreateRequest(Long movieId, Long hallId, List<LocalDateTime> startTimes) {
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTimes = startTimes;
    }

}