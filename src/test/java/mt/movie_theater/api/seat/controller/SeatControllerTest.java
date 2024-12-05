package mt.movie_theater.api.seat.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import mt.movie_theater.api.seat.request.SeatListCreateRequest;
import mt.movie_theater.api.seat.service.SeatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = SeatController.class)
class SeatControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SeatService seatService;

    @DisplayName("신규 좌석 리스트를 등록한다.")
    @Test
    void createSeatList() throws Exception {
        //given
        SeatListCreateRequest request = SeatListCreateRequest.builder()
                .hallId(1L)
                .rows(2)
                .columns(3)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/seats/new"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("신규 좌석 리스트를 등록할 때, 상영관은 필수 입력값이다.")
    @Test
    void createSeatListNoHall() throws Exception {
        //given
        SeatListCreateRequest request = SeatListCreateRequest.builder()
                .rows(2)
                .columns(3)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/seats/new"))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상영관 ID는 필수 입력값입니다."));
    }

    @DisplayName("좌석 리스트를 조회한다.")
    @Test
    void getSeatList() throws Exception{
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/seats"))
                        .param("screeningId", "1")
                        .param("hallId", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}