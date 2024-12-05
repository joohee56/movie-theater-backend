package mt.movie_theater.domain.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mt.movie_theater.domain.theater.Theater;

@Getter
@AllArgsConstructor
public class TheaterScreeningCountDto {
    private Theater theater;
    private Long count;
}
