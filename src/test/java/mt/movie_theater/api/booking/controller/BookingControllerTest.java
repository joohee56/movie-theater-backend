package mt.movie_theater.api.booking.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mt.movie_theater.api.booking.request.BookingHoldRequest;
import mt.movie_theater.api.booking.service.BookingService;
import mt.movie_theater.api.payment.request.ConfirmBookingRequest;
import mt.movie_theater.api.user.constants.SessionConstants;
import mt.movie_theater.domain.payment.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @DisplayName("임시 에매를 등록한다.")
    @Test
    void holdBooking() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);
        BookingHoldRequest request = BookingHoldRequest.builder()
                .screeningId(1L)
                .seatIds(List.of(1L, 2L))
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/bookings/booking/hold"))
                .session(session)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("임시 에매를 등록할 때, 상영시간은 필수 입력값이다.")
    @Test
    void holdBookingNoScreening() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);
        BookingHoldRequest request = BookingHoldRequest.builder()
                .seatIds(List.of(1L, 2L))
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/bookings/booking/hold"))
                        .session(session)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("상영시간 ID는 필수 입력값입니다."));
    }

    @DisplayName("예매 사후 검증 후 예매를 확정한다.")
    @Test
    void confirmBookingAndPaymentHistory() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);
        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .bookingId(1L)
                .impId("impId")
                .amount(10000L)
                .bookingNumber("bookingNumber")
                .payTime(10000L)
                .payMethod("card")
                .currency(Currency.KRW)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/bookings/booking/confirm"))
                        .session(session)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("예매 사후 검증 후 예매를 확정활 때, 에매는 필수 입력값이다.")
    @Test
    void confirmBookingAndPaymentHistoryWithNoBooking() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);
        ConfirmBookingRequest request = ConfirmBookingRequest.builder()
                .impId("impId")
                .amount(10000L)
                .bookingNumber("bookingNumber")
                .payTime(10000L)
                .payMethod("card")
                .currency(Currency.KRW)
                .build();

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/bookings/booking/confirm"))
                        .session(session)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("예매 ID는 필수 입력값입니다."));
    }

    @DisplayName("예매를 조회한다.")
    @Test
    void getBooking() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/bookings/booking/1"))
                .session(session)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("회원의 예매 내역을 조회한다.")
    @Test
    void getBookingHistory() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/bookings/user"))
                        .session(session)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("예매를 취소하고 회원의 에매 내역을 조회한다.")
    @Test
    void cancelBookingAndGetBookingHistory() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstants.LOGIN_USER_ID, 1L);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.get(("/api/v1/bookings/cancel/1"))
                        .session(session)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }
}