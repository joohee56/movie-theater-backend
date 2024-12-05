package mt.movie_theater.api.hall.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.movie.ScreeningType;
import mt.movie_theater.domain.theater.Theater;

@Getter
@NoArgsConstructor
public class HallSeatsCreateRequest {

    @NotNull(message = "영화관 ID는 필수 입력값입니다.")
    private Long theaterId;

    @NotBlank(message = "상영관 이름은 필수 입력값입니다.")
    private String name;

    @NotNull(message = "상영타입은 필수 입력값입니다.")
    private ScreeningType screeningType;

    @PositiveOrZero(message = "추가 요금은 0 또는 양수여야 합니다.")
    private int hallTypeModifier;

    @PositiveOrZero(message = "좌석의 행 갯수는 0 또는 양수여야 합니다.")
    private int rows;

    @PositiveOrZero(message = "좌석의 열 갯수는 0 또는 양수여야 합니다.")
    private int columns;

    @Builder
    public HallSeatsCreateRequest(Long theaterId, String name, ScreeningType screeningType, int hallTypeModifier,
                                  int rows,
                                  int columns) {
        this.theaterId = theaterId;
        this.name = name;
        this.screeningType = screeningType;
        this.hallTypeModifier = hallTypeModifier;
        this.rows = rows;
        this.columns = columns;
    }

    public Hall toEntity(Theater theater) {
        return Hall.builder()
                .theater(theater)
                .name(name)
                .screeningType(screeningType)
                .hallTypeModifier(hallTypeModifier)
                .build();
    }
}
