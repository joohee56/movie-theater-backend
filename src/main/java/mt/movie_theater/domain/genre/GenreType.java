package mt.movie_theater.domain.genre;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenreType {
    ACTION("액션"),
    COMEDY("코미디"),
    DRAMA("드라마"),
    HORROR("공포"),
    THRILLER("스릴러"),
    ROMANCE("로맨스"),
    SF("SF"),
    DOCUMENTARY("다큐멘터리");

    private final String text;
}
