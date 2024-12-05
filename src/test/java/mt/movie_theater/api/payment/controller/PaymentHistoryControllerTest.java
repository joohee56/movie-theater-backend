package mt.movie_theater.api.payment.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import mt.movie_theater.api.payment.request.PreparePaymentRequest;
import mt.movie_theater.api.payment.service.PaymentHistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = PaymentHistoryController.class)
class PaymentHistoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PaymentHistoryService paymentHistoryService;

    @DisplayName("결제 사전 검증을 등록한다.")
    @Test
    void preparePayment() throws Exception {
        //given
        PreparePaymentRequest request = PreparePaymentRequest.builder()
                .bookingNumber("bookingNumber")
                .amount(1L)
                .build();
        when(paymentHistoryService.preparePayment(request.getBookingNumber(), request.getAmount()))
                .thenReturn(true);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/prepare")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("결제 사전 검증 등록을 실패한다.")
    @Test
    void preparePaymentFail() throws Exception {
        //given
        PreparePaymentRequest request = PreparePaymentRequest.builder()
                .bookingNumber("bookingNumber")
                .amount(1L)
                .build();
        when(paymentHistoryService.preparePayment(request.getBookingNumber(), request.getAmount()))
                .thenReturn(false);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/prepare")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("결제 사전 검증 등록을 실패했습니다."));
    }

    @DisplayName("결제 사전 검증을 등록할 때, 예매번호는 필수 입력값이다.")
    @Test
    void preparePaymentNoBookingNumber() throws Exception {
        //given
        PreparePaymentRequest request = PreparePaymentRequest.builder()
                .amount(1L)
                .build();
        when(paymentHistoryService.preparePayment(request.getBookingNumber(), request.getAmount()))
                .thenReturn(true);

        //when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/prepare")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("예매번호는 필수 입력값입니다."));
    }
}