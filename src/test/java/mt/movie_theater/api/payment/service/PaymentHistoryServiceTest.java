package mt.movie_theater.api.payment.service;

import static mt.movie_theater.domain.payment.PayStatus.CANCELED;
import static mt.movie_theater.domain.payment.PayStatus.COMPLETED;
import static mt.movie_theater.domain.payment.PayStatus.FAILED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import java.io.IOException;
import java.math.BigDecimal;
import mt.movie_theater.api.exception.CancelPaymentException;
import mt.movie_theater.api.exception.PaymentValidationException;
import mt.movie_theater.api.exception.PreparePaymentException;
import mt.movie_theater.domain.payment.PayStatus;
import mt.movie_theater.domain.payment.PaymentHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTest {
    @Mock
    private IamportResponse<Prepare> prepareResponse;
    @Mock
    private IamportResponse<Payment> paymentResponse;
    @Mock
    private Payment payment;
    @Mock
    private IamportClient iamportClient;
    @Spy
    @InjectMocks
    private PaymentHistoryService paymentHistoryService;

    @DisplayName("결제 사전 등록이 성공한다.")
    @Test
    void preparePayment() throws IamportResponseException, IOException {
        //given
        given(iamportClient.postPrepare(any()))
                        .willReturn(prepareResponse);
        given(prepareResponse.getCode())
                .willReturn(0);

        //when
        boolean result = paymentHistoryService.preparePayment("bookingNumber", 10000L);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("결제 사전 등록이 실패한다.")
    @Test
    void preparePaymentFail() throws IamportResponseException, IOException {
        //given
        given(iamportClient.postPrepare(any()))
                .willReturn(prepareResponse);
        given(prepareResponse.getCode())
                .willReturn(-1);

        //when
        boolean result = paymentHistoryService.preparePayment("bookingNumber", 10000L);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("결제 사전 등록 중 예외 발생 시 예외가 전환된다.")
    @Test
    void preparePaymentException() throws IamportResponseException, IOException {
        //given
        given(iamportClient.postPrepare(any()))
                .willThrow(IOException.class);

        //when, then
        assertThatThrownBy(() -> paymentHistoryService.preparePayment("bookingNumber", 10000L))
                .isInstanceOf(PreparePaymentException.class)
                .hasMessage("결제 사전 등록 중 예외가 발생했습니다.");
    }

    @DisplayName("결제를 실패 처리한다.")
    @Test
    void failPayment() {
        //given
        PaymentHistory paymentHistory = createPaymentHistory(COMPLETED);
        doReturn(true)
                .when(paymentHistoryService)
                .cancelIamportPayment(anyString(), anyString());

        //when
        paymentHistoryService.failPayment(paymentHistory.getImpId(), paymentHistory, "결제 실패 처리 테스트");

        //then
        assertThat(paymentHistory.getPayStatus()).isEqualTo(FAILED);
    }

    @DisplayName("결제를 실패처리할 때, 결제 취소가 실패하면 예외가 발생한다.")
    @Test
    void failPaymentWithException() {
        //given
        PaymentHistory paymentHistory = createPaymentHistory(COMPLETED);
        doReturn(false)
                .when(paymentHistoryService)
                .cancelIamportPayment(anyString(), anyString());

        //when, then
        assertThatThrownBy(() -> paymentHistoryService.failPayment(paymentHistory.getImpId(), paymentHistory, "결제 실채 처리 중 예외 발생"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Fail Payment] 결제 취소가 실패했습니다.");
        assertThat(paymentHistory.getPayStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("결제를 취소 처리한다.")
    @Test
    void cancelPayment() {
        //given
        PaymentHistory paymentHistory = createPaymentHistory(COMPLETED);
        doReturn(true)
                .when(paymentHistoryService)
                .cancelIamportPayment(anyString(), anyString());

        //when
        paymentHistoryService.cancelPayment(paymentHistory.getImpId(), paymentHistory, "결제 취소 처리 테스트");

        //then
        assertThat(paymentHistory.getPayStatus()).isEqualTo(CANCELED);
    }

    @DisplayName("결제를 취소처리할 때, 결제 취소가 실패하면 예외가 발생한다.")
    @Test
    void cancelPaymentWithException() {
        //given
        PaymentHistory paymentHistory = createPaymentHistory(COMPLETED);
        doReturn(false)
                .when(paymentHistoryService)
                .cancelIamportPayment(anyString(), anyString());

        //when, then
        assertThatThrownBy(() -> paymentHistoryService.cancelPayment(paymentHistory.getImpId(), paymentHistory, "결제 실채 처리 중 예외 발생"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Cancel Payment] 결제 취소가 실패했습니다.");
        assertThat(paymentHistory.getPayStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("Iamport를 이용하여 결제를 취소한다.")
    @Test
    void cancelIamportPayment() throws IamportResponseException, IOException {
        //given
        given(iamportClient.cancelPaymentByImpUid(any())).willReturn(paymentResponse);
        given(paymentResponse.getResponse()).willReturn(payment);
        given(payment.getStatus()).willReturn("cancelled");

        //when
        boolean result = paymentHistoryService.cancelIamportPayment("impId", "결제 취소 사유");

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("Iamport를 이용하여 결제를 취소할 때, 실패할 경우 false를 반환한다.")
    @Test
    void cancelIamportPaymentFail() throws IamportResponseException, IOException {
        //given
        given(iamportClient.cancelPaymentByImpUid(any())).willReturn(paymentResponse);
        given(paymentResponse.getResponse()).willReturn(payment);
        given(payment.getStatus()).willReturn("fail");

        //when
        boolean result = paymentHistoryService.cancelIamportPayment("impId", "결제 취소 사유");

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("Iamport를 이용하여 결제를 취소할 때, 반환값이 없을 경우 false를 반환한다.")
    @Test
    void cancelIamportPaymentNull() throws IamportResponseException, IOException {
        //given
        given(iamportClient.cancelPaymentByImpUid(any())).willReturn(null);

        //when
        boolean result = paymentHistoryService.cancelIamportPayment("impId", "결제 취소 사유");

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("Iamport를 이용하여 결제를 취소할 때, 예외가 발생할 경우 예외를 전환한다.")
    @Test
    void cancelIamportPaymentWithException() throws IamportResponseException, IOException {
        //given
        doThrow(IamportResponseException.class)
                .when(iamportClient)
                .cancelPaymentByImpUid(any());

        //when
        assertThatThrownBy(() -> paymentHistoryService.cancelIamportPayment("impId", "결제 취소 사유"))
                .isInstanceOf(CancelPaymentException.class)
                .hasMessage("[Iamport] 결제 취소 중 예외가 발생했습니다.");
    }

    @DisplayName("사용자가 결제 요청한 금액과 실제 처리된 금액을 비교할 때, 같은 경우 성공한다.")
    @Test
    void validatePaymentAmountSuccess() throws IamportResponseException, IOException {
        //given
        given(iamportClient.paymentByImpUid(any()))
                .willReturn(paymentResponse);
        setupPaymentResponseAmount(10000L);

        //when, then
        assertThat(paymentHistoryService.validatePaymentAmount("paymentId", 10000L)).isTrue();
    }

    @DisplayName("사용자가 결제 요청한 금액과 실제 처리된 금액을 비교할 때, 다른 경우 실패한다.")
    @Test
    void validatePaymentAmountFail() throws IamportResponseException, IOException {
        //given
        given(iamportClient.paymentByImpUid(any()))
                .willReturn(paymentResponse);
        setupPaymentResponseAmount(20000L);

        //when, then
        assertThat(paymentHistoryService.validatePaymentAmount("paymentId", 10000L)).isFalse();
    }

    @DisplayName("사용자가 결제 요청한 금액과 실제 처리된 금액을 비교할 때, 예외가 발생할 경우 예외를 전환한다.")
    @Test
    void validatePaymentAmountWithException() throws IamportResponseException, IOException {
        //given
        doThrow(IamportResponseException.class)
                .when(iamportClient)
                .paymentByImpUid(anyString());

        //when, then
        assertThatThrownBy(() -> paymentHistoryService.validatePaymentAmount("paymentId", 10000L))
                .isInstanceOf(PaymentValidationException.class)
                .hasMessage("결제 사후 검증 중 예외가 발생했습니다.");
    }

    private void setupPaymentResponseAmount(Long amount) {
        given(paymentResponse.getResponse())
                .willReturn(payment);
        given(payment.getAmount())
                .willReturn(BigDecimal.valueOf(amount));
    }

    private PaymentHistory createPaymentHistory(PayStatus payStatus) {
        return PaymentHistory.builder()
                .impId("impId")
                .payStatus(payStatus)
                .build();
    }
}