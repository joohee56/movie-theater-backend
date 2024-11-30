package mt.movie_theater.domain.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mt.movie_theater.domain.theater.Region;

@Getter
@AllArgsConstructor
public class RegionScreeningCountDto {
    private Region region;
    private Long count;
}
