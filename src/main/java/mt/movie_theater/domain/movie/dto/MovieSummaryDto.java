package mt.movie_theater.domain.movie.dto;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.movie.Movie;

@Getter
public class MovieSummaryDto {
    private Long movieId;
    private String title;
    private String ageRatingDisplay;

    @Builder
    private MovieSummaryDto(Long movieId, String title, String ageRatingDisplay) {
        this.movieId = movieId;
        this.title = title;
        this.ageRatingDisplay = ageRatingDisplay;
    }

    public static MovieSummaryDto create(Movie movie) {
        return MovieSummaryDto.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .ageRatingDisplay(movie.getAgeRating().getDisplay())
                .build();
    }
}
