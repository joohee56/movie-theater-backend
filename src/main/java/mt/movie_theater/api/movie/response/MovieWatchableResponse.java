package mt.movie_theater.api.movie.response;

import lombok.Builder;
import lombok.Getter;
import mt.movie_theater.domain.movie.Movie;

@Getter
public class MovieWatchableResponse {
    private Long movieId;
    private String title;
    private String posterUrl;
    private String ageRatingDisplay;
    private boolean isWatchable;

    @Builder
    private MovieWatchableResponse(Long movieId, String title, String posterUrl, String ageRatingDisplay,
                                  boolean isWatchable) {
        this.movieId = movieId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.ageRatingDisplay = ageRatingDisplay;
        this.isWatchable = isWatchable;
    }

    public static MovieWatchableResponse create(Movie movie, boolean isWatchable) {
        return MovieWatchableResponse.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .posterUrl(movie.getPosterUrl())
                .ageRatingDisplay(movie.getAgeRating().getDisplay())
                .isWatchable(isWatchable)
                .build();
    }
}
