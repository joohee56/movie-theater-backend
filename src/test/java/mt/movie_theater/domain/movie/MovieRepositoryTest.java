package mt.movie_theater.domain.movie;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import mt.movie_theater.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MovieRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private MovieRepository movieRepository;

    @DisplayName("가장 최근에 등록된 8개의 영화를 조회한다.")
    @Test
    void findTop8ByOrderByCreatedAtDesc() {
        //given
        Movie movie1 = createMovie();
        Movie movie2 = createMovie();
        Movie movie3 = createMovie();
        Movie movie4 = createMovie();
        Movie movie5 = createMovie();
        Movie movie6 = createMovie();
        Movie movie7 = createMovie();
        Movie movie8 = createMovie();
        Movie movie9 = createMovie();
        Movie movie10 = createMovie();

        //when
        List<Movie> movies = movieRepository.findTop8ByOrderByCreatedAtDesc();

        //then
        Assertions.assertThat(movies).hasSize(8)
                .extracting("id")
                .containsExactly(
                        movie10.getId(), movie9.getId(), movie8.getId(), movie7.getId()
                        ,movie6.getId(), movie5.getId(), movie4.getId(), movie3.getId()
                );
    }

    private Movie createMovie() {
        Movie movie = Movie.builder().build();
        return movieRepository.save(movie);
    }

}