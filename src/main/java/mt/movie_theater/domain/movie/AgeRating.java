package mt.movie_theater.domain.movie;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeRating {
    ALL("전체관람가", "ALL"),
    AGE_12("12세이상관람가", "12"),
    AGE_15("15세이상관람가", "15"),
    AGE_19("청소년관람불가", "19");

    private final String text;
    private final String display;
}
