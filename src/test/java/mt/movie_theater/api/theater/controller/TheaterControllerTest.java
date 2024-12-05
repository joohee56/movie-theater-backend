package mt.movie_theater.api.theater.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import mt.movie_theater.api.theater.request.TheaterCreateRequest;
import mt.movie_theater.api.theater.service.TheaterService;
import mt.movie_theater.domain.theater.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = TheaterController.class)
class TheaterControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TheaterService theaterService;

    @DisplayName("새 영화관을 등록한다.")
    @Test
    void createTheater() throws Exception {
        //given
        TheaterCreateRequest request = TheaterCreateRequest.builder()
                .name("상암월드컵경기장")
                .address("서울특별시 마포구 월드컵로 240, (성산동) 월드컵주경기장 1층")
                .region(Region.SEOUL)
                .latitude(37.5683F)
                .longitude(126.8974F)
                .contactNumber("1544-0070")
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/theaters/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("새 영화관을 등록할 때, 영화관 이름은 필수 입력값이다.")
    @Test
    void createTheaterWithNoName() throws Exception {
        //given
        TheaterCreateRequest request = TheaterCreateRequest.builder()
                .address("서울특별시 마포구 월드컵로 240, (성산동) 월드컵주경기장 1층")
                .region(Region.SEOUL)
                .latitude(37.5683F)
                .longitude(126.8974F)
                .contactNumber("1544-0070")
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/theaters/new")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("영화관 이름은 필수 입력값입니다."));
    }

    @DisplayName("지역 리스트와 각 지역별 영화관 갯수를 조회한다.")
    @Test
    void getRegionsWithTheaterCount() throws Exception{
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/theaters/regions")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("지역에 해당하는 영화관 리스트를 조회한다.")
    @Test
    void getTheatersByRegion() throws Exception{
        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/theaters/regions/SEOUL")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }
}