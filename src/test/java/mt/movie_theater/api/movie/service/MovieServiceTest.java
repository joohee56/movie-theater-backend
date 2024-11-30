package mt.movie_theater.api.movie.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import mt.movie_theater.IntegrationTestSupport;
import mt.movie_theater.api.movie.request.MovieCreateRequest;
import mt.movie_theater.api.movie.response.MovieResponse;
import mt.movie_theater.api.movie.response.MovieWatchableResponse;
import mt.movie_theater.domain.genre.Genre;
import mt.movie_theater.domain.genre.GenreRepository;
import mt.movie_theater.domain.genre.GenreType;
import mt.movie_theater.domain.hall.Hall;
import mt.movie_theater.domain.hall.HallRepository;
import mt.movie_theater.domain.movie.AgeRating;
import mt.movie_theater.domain.movie.Movie;
import mt.movie_theater.domain.movie.MovieRepository;
import mt.movie_theater.domain.movie.ScreeningType;
import mt.movie_theater.domain.screening.Screening;
import mt.movie_theater.domain.screening.ScreeningRepository;
import mt.movie_theater.domain.theater.Theater;
import mt.movie_theater.domain.theater.TheaterRepository;
import mt.movie_theater.util.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MovieServiceTest extends IntegrationTestSupport {
    @Autowired
    private MovieService movieService;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private ScreeningRepository screeningRepository;
    @MockBean
    private S3Uploader s3Uploader;

    @DisplayName("영화를 등록한다.")
    @Test
    void createMovieTest() {
        //given
        createGenre(GenreType.ROMANCE);
        createGenre(GenreType.DRAMA);

        when(s3Uploader.upload(any(), anyString()))
                .thenReturn("test@test.com");

        MovieCreateRequest request = MovieCreateRequest.builder()
                .title("청설")
                .subTitle("Hear me")
                .description("손으로 설렘을 말하고")
                .releaseDate(LocalDate.of(2024, 11, 28))
                .durationMinutes(108)
                .ageRating(AgeRating.ALL)
                .director("조선호")
                .screeningType(ScreeningType.TWO_D)
                .standardPrice(10000)
                .genreTypes(List.of(GenreType.DRAMA, GenreType.ROMANCE))
                .actors(List.of("홍경", "노윤서"))
                .build();

        //when
        MovieResponse response = movieService.createMovie(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("title", "releaseDate", "durationMinutes", "posterUrl", "ageRating", "ageRatingDisplay")
                .contains("청설", LocalDate.of(2024, 11, 28), 108L, "test@test.com", "ALL", "ALL");
        assertThat(response.getMovieGenres()).hasSize(2)
                .containsExactlyInAnyOrder("로맨스", "드라마");
        assertThat(response.getMovieActors()).hasSize(2)
                .containsExactlyInAnyOrder("홍경", "노윤서");
    }

    @DisplayName("전체 영화를 조회한다.")
    @Test
    void getAllMovies() {
        //given
        Movie movie1 = createMovie("청설");
        Movie movie2 = createMovie("아마존 활명수");
        Movie movie3 = createMovie("고래와 나");
        movieRepository.saveAll(List.of(movie1, movie2, movie3));

        //when
        List<MovieResponse> response = movieService.getAllMovies();

        //then
        assertThat(response).hasSize(3);
    }

    @DisplayName("가장 최신 영화 8개를 조회한다.")
    @Test
    void getRecentMovies() {
        //given
        createMovie("영화1");
        createMovie("영화2");
        createMovie("영화3");
        createMovie("영화4");
        createMovie("영화5");
        createMovie("영화6");
        createMovie("영화7");
        createMovie("영화8");
        createMovie("영화9");
        createMovie("영화10");

        //when
        List<MovieResponse> movies = movieService.getRecentMovies();

        //then
        assertThat(movies).hasSize(8);
        assertThat(movies)
                .extracting("title")
                .containsExactly("영화10", "영화9", "영화8", "영화7", "영화6", "영화5", "영화4", "영화3");
    }

    @DisplayName("전체 영화 리스트와 각 영화별 상영시간 포함 유무를 조회한다. 상영시간 조건에는 날짜, (영화관)이 있다.")
    @Test
    void getMoviesWithIsWatchable() {
        //given
        Movie movie1 = createMovie("청설");
        Movie movie2 = createMovie("아마존 활명수");
        Movie movie3 = createMovie("보통의 가족");

        Theater theater = createTheater();
        Hall hall = createHall(theater);
        createScreening(movie1, hall, LocalDateTime.of(2024, 11, 1, 0, 0));
        createScreening(movie2, hall, LocalDateTime.of(2024, 11, 1, 23, 59));
        //날짜 미해당
        createScreening(movie3, hall, LocalDateTime.of(2024, 11, 2, 0, 0));
        LocalDate date = LocalDate.of(2024, 11, 1);

        //when
        List<MovieWatchableResponse> movies = movieService.getMoviesWithIsWatchable(date, theater.getId());

        //then
        assertThat(movies).hasSize(3)
                .extracting("title", "isWatchable")
                .containsExactlyInAnyOrder(
                        tuple("청설", true),
                        tuple("아마존 활명수", true),
                        tuple("보통의 가족", false)
                );
    }

    private Movie createMovie(String title) {
        Movie movie = Movie.builder()
                .title(title)
                .releaseDate(LocalDate.of(2024, 11, 28))
                .movieGenres(List.of())
                .movieActors(List.of())
                .ageRating(AgeRating.ALL)
                .screeningType(ScreeningType.TWO_D)
                .durationMinutes(Duration.ofMinutes(108))
                .build();
        return movieRepository.save(movie);
    }
    private Genre createGenre(GenreType genreType) {
        Genre genre = Genre.builder()
                .type(genreType)
                .build();
        return genreRepository.save(genre);
    }
    private Theater createTheater() {
        Theater theater = Theater.builder()
                .build();
        return theaterRepository.save(theater);
    }

    private Hall createHall(Theater theater) {
        Hall hall = Hall.builder()
                .theater(theater)
                .build();
        return hallRepository.save(hall);
    }
    private Screening createScreening(Movie movie, Hall hall, LocalDateTime startTime) {
        Screening screening = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(startTime)
                .build();
        return screeningRepository.save(screening);
    }

}