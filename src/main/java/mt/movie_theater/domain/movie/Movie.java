package mt.movie_theater.domain.movie;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.api.movie.request.MovieCreateRequest;
import mt.movie_theater.domain.BaseEntity;
import mt.movie_theater.domain.genre.Genre;
import mt.movie_theater.domain.movieactor.MovieActor;
import mt.movie_theater.domain.moviegenre.MovieGenre;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String subTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate releaseDate;

    private Duration durationMinutes;

    private String posterUrl;

    @Enumerated(EnumType.STRING)
    private AgeRating ageRating;

    @Column(length = 50)
    private String director;

    @Enumerated(EnumType.STRING)
    private ScreeningType screeningType;

    private int standardPrice;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieActor> actors = new ArrayList<>();

    public void addMovieGenre(MovieGenre movieGenre) {
        this.movieGenres.add(movieGenre);
    }

    public void addMovieActor(MovieActor movieActor) {
        this.actors.add(movieActor);
    }

    @Builder
    private Movie(String title, String subTitle, String description, LocalDate releaseDate, Duration durationMinutes,
                  String posterUrl, AgeRating ageRating, String director, ScreeningType screeningType,
                  List<MovieGenre> movieGenres, List<MovieActor> movieActors, int standardPrice) {
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.releaseDate = releaseDate;
        this.durationMinutes = durationMinutes;
        this.posterUrl = posterUrl;
        this.ageRating = ageRating;
        this.director = director;
        this.screeningType = screeningType;
        this.standardPrice = standardPrice;
        this.movieGenres = movieGenres != null ? movieGenres : new ArrayList<>();
        this.actors = movieActors != null ? movieActors : new ArrayList<>();
    }

    public static Movie create(MovieCreateRequest request, List<Genre> genres, String posterUrl) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .subTitle(request.getSubTitle())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .durationMinutes(Duration.ofMinutes(request.getDurationMinutes()))
                .posterUrl(posterUrl)
                .ageRating(request.getAgeRating())
                .director(request.getDirector())
                .screeningType(request.getScreeningType())
                .standardPrice(request.getStandardPrice())
                .build();
        for (Genre genre : genres) {
            movie.addMovieGenre(MovieGenre.create(movie, genre));
        }
        for (String name : request.getActors()) {
            movie.addMovieActor(MovieActor.create(movie, name));
        }
        return movie;
    }
}
