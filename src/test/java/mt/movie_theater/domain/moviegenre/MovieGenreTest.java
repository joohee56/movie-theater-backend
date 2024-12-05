package mt.movie_theater.domain.moviegenre;

import static org.assertj.core.api.Assertions.assertThat;

import mt.movie_theater.domain.movie.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MovieGenreTest {

    @DisplayName("영화를 지정한다.")
    @Test
    void setMovie() {
        //given
        Movie movie = createMovie();
        MovieGenre  movieGenre = createMovieGenre();
        assertThat(movieGenre.getMovie()).isNull();

        //when
        movieGenre.setMovie(movie);

        //then
        assertThat(movieGenre.getMovie()).isEqualTo(movie);
    }

    private Movie createMovie() {
        return Movie.builder().build();
    }
    private MovieGenre createMovieGenre() {
        return MovieGenre.builder().build();
    }

}