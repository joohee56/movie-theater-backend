package mt.movie_theater.api.screening.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import mt.movie_theater.api.screening.request.ScreeningCreateRequest;
import mt.movie_theater.api.screening.service.ScreeningService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = ScreeningController.class)
class ScreeningControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ScreeningService screeningService;

    @DisplayName("신규 상영시간 리스트를 등록한다.")
    @Test
    void createScreenings() throws Exception {
        //given
        ScreeningCreateRequest request = ScreeningCreateRequest.builder()
                .movieId(1L)
                .hallId(1L)
                .startTimes(List.of(LocalDateTime.of(2024, 11, 1, 13, 0), LocalDateTime.of(2024, 11, 1, 15, 0)))
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/screenings/new"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("신규 상영시간 리스트를 등록할 때, 영화는 필수 입력값이다.")
    @Test
    void createScreeningsNoMovie() throws Exception {
        //given
        ScreeningCreateRequest request = ScreeningCreateRequest.builder()
                .hallId(1L)
                .startTimes(List.of(LocalDateTime.of(2024, 11, 1, 13, 0), LocalDateTime.of(2024, 11, 1, 15, 0)))
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/screenings/new"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("영화 ID는 필수 입력값입니다."));
    }

    @DisplayName("지역 리스트와 각 지역별 상영시간 갯수를 조회한다.")
    @Test
    void getRegionsWithScreeningCount() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/screenings/region/screeningCount"))
                        .param("date", "2024.11.01")
                        .param("movieId", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("지역 리스트와 각 지역별 상영시간 갯수를 조회할 때, 날짜는 필수 입력값이다.")
    @Test
    void getRegionsWithScreeningCountNoDate() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/screenings/region/screeningCount"))
                        .param("movieId", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("날짜는 필수 입력값입니다."));
    }

    @DisplayName("영화관 리스트와 각 영화관별 상영시간 갯수를 조회한다.")
    @Test
    void getTheatersWithScreeningCount() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/screenings/theater/screeningCount"))
                        .param("date", "2024.11.01")
                        .param("movieId", "1")
                        .param("region", "SEOUL")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("영화관 리스트와 각 영화관별 상영시간 갯수를 조회할 때, 날짜는 필수 입력값이다.")
    @Test
    void getTheatersWithScreeningCountNoDate() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/screenings/theater/screeningCount"))
                        .param("movieId", "1")
                        .param("region", "SEOUL")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("날짜는 필수 입력값입니다."));
    }

    @DisplayName("상영시간 리스트를 조회한다.")
    @Test
    void getScreenings() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/screenings"))
                        .param("date", "2024.11.01")
                        .param("movieId", "1")
                        .param("theaterId", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("상영시간 리스트를 조회할 때, 날짜는 필수 입력값이다.")
    @Test
    void getScreeningsNoDate() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/screenings"))
                        .param("movieId", "1")
                        .param("theaterId", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("날짜는 필수 입력값입니다."));
    }

    @DisplayName("상영시간과 최종 결제 금액을 조회한다.")
    @Test
    void getScreeningAndTotalPrice() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/screenings/screening/1"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}