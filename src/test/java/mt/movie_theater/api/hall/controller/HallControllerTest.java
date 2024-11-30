package mt.movie_theater.api.hall.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import mt.movie_theater.api.hall.request.HallCreateRequest;
import mt.movie_theater.api.hall.request.HallSeatsCreateRequest;
import mt.movie_theater.api.hall.service.HallService;
import mt.movie_theater.domain.movie.ScreeningType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = HallController.class)
class HallControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private HallService hallService;

    @DisplayName("새 상영관을 등록한다.")
    @Test
    void createHall() throws Exception {
        //given
        HallCreateRequest request = HallCreateRequest.builder()
                .theaterId(1L)
                .name("1관")
                .screeningType(ScreeningType.TWO_D)
                .hallTypeModifier(3000)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/halls/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("새 상영관을 등록할 때, 영화관은 필수 입력값이다.")
    @Test
    void createHallWithNoTheater() throws Exception {
        //given
        HallCreateRequest request = HallCreateRequest.builder()
                .name("1관")
                .screeningType(ScreeningType.TWO_D)
                .hallTypeModifier(3000)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/halls/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("영화관 ID는 필수 입력값입니다."));
    }

    @DisplayName("새 상영관과 좌석리스트를 등록한다.")
    @Test
    void createHallWithSeats() throws Exception {
        //given
        HallSeatsCreateRequest request = HallSeatsCreateRequest.builder()
                .theaterId(1L)
                .name("1관")
                .screeningType(ScreeningType.TWO_D)
                .hallTypeModifier(3000)
                .rows(3)
                .columns(2)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/halls/new/seats")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("새 상영관과 좌석리스트를 등록할 때, 영화관은 필수 입력값이다.")
    @Test
    void createHallWithSeatsWithNoTheater() throws Exception {
        //given
        HallSeatsCreateRequest request = HallSeatsCreateRequest.builder()
                .name("1관")
                .screeningType(ScreeningType.TWO_D)
                .hallTypeModifier(3000)
                .rows(3)
                .columns(2)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/halls/new/seats")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("영화관 ID는 필수 입력값입니다."));
    }

    @DisplayName("영화관에 해당하는 상영관 리스트를 조회한다.")
    @Test
    void getHalls() throws Exception {
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/halls/theater/1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}