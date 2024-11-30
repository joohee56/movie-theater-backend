package mt.movie_theater.api.movie.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import mt.movie_theater.api.movie.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = MovieController.class)
class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MovieService movieService;

    @DisplayName("신규 영화를 등록한다.")
    @Test
    void createMovie() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "posterImage", // 필드 이름
                "test.txt", // 원래 파일 이름
                MediaType.TEXT_PLAIN_VALUE, // 파일 타입
                "Hello, World!".getBytes() // 파일 내용
        );

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/movies/new")
                        .file(file)
                        .param("title", "청설")
                        .param("subTitle", "Hear Me: Our Summer")
                        .param("description", "가슴으로 사랑을 느끼는, 청량한 설렘의 순간")
                        .param("releaseDate", "2024.10.27")
                        .param("durationMinutes", "108")
                        .param("ageRating", "ALL")
                        .param("director", "조선호")
                        .param("screeningType", "TWO_D")
                        .param("standardPrice", "10000")
                        .param("genreTypes", "ROMANCE")
                        .param("genreTypes", "DRAMA")
                        .param("actors", "홍경")
                        .param("actors", "노윤서")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("신규 영화를 등록할 때, 영화 제목은 필수 입력값이다.")
    @Test
    void createMovieWithNoMovieTitle() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "posterImage", // 필드 이름
                "test.txt", // 원래 파일 이름
                MediaType.TEXT_PLAIN_VALUE, // 파일 타입
                "Hello, World!".getBytes() // 파일 내용
        );

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/movies/new")
                        .file(file)
                        .param("subTitle", "Hear Me: Our Summer")
                        .param("description", "가슴으로 사랑을 느끼는, 청량한 설렘의 순간")
                        .param("releaseDate", "2024.10.27")
                        .param("durationMinutes", "108")
                        .param("ageRating", "ALL")
                        .param("director", "조선호")
                        .param("screeningType", "TWO_D")
                        .param("standardPrice", "10000")
                        .param("genreTypes", "ROMANCE")
                        .param("genreTypes", "DRAMA")
                        .param("actors", "홍경")
                        .param("actors", "노윤서")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("영화제목은 필수 입력값입니다."));
    }

    @DisplayName("모든 영화 리스트를 조회한다.")
    @Test
    void getAllMovies() throws Exception {
        ///when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movies")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("가장 최신 영화 리스트를 조회한다.")
    @Test
    void getRecentMovies() throws Exception {
        ///when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movies/recent")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("예매 가능 여부와 함께 영화 리스트를 조회한다.")
    @Test
    void getMoviesWithIsWatchable() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movies/watchable")
                        .param("date", "2024.11.29")
                        .param("theaterId", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("예매 가능 여부와 함께 영화 리스트를 조회할 때, 날짜는 필수 입력값이다.")
    @Test
    void getMoviesWithIsWatchableNoDate() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/movies/watchable")
                        .param("theaterId", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("날짜는 필수 입력값입니다."));
    }
}