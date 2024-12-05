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
public class HallCreateRequest {

    @NotNull(message = "영화관 ID는 필수 입력값입니다.")
    private Long theaterId;

    @NotBlank(message = "상영관 이름은 필수 입력값입니다.")
    private String name;

    @NotNull(message = "상영 타입은 필수 입력값입니다.")
    private ScreeningType screeningType;

    @PositiveOrZero(message = "추가요금은 0 또는 양수여야 합니다.")
    private int hallTypeModifier;

    @Builder
    public HallCreateRequest(Long theaterId, String name, ScreeningType screeningType, int hallTypeModifier) {
        this.theaterId = theaterId;
        this.name = name;
        this.screeningType = screeningType;
        this.hallTypeModifier = hallTypeModifier;
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
