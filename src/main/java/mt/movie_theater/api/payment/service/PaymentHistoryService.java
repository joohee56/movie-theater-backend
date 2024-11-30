package mt.movie_theater.api.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.exception.CancelPaymentException;
import mt.movie_theater.api.exception.PaymentValidationException;
import mt.movie_theater.api.exception.PreparePaymentException;
import mt.movie_theater.domain.payment.PaymentHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-imp.properties")
@Transactional
public class PaymentHistoryService {

    @Value("${IMP_API_KEY}")
    private String apiKey;

    @Value("${IMP_API_SECRET_KEY}")
    private String apiSecretKey;

    private IamportClient iamportClient;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, apiSecretKey);
    }

    /**
     * 결제 사전 검증
     */
    public boolean preparePayment(String bookingNumber, Long amount) {
        PrepareData prepareData = new PrepareData(bookingNumber, BigDecimal.valueOf(amount));
        try {
            IamportResponse<Prepare> response = iamportClient.postPrepare(prepareData);
            if (response.getCode() == 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new PreparePaymentException("결제 사전 등록 중 예외가 발생했습니다.", e);
        }
    }

    public void failPayment(String impId, PaymentHistory paymentHistory, String reason) {
        if (!cancelIamportPayment(impId, reason)) {
            throw new IllegalStateException("[Fail Payment] 결제 취소가 실패했습니다.");
        }
        paymentHistory.fail();
    }

    public void cancelPayment(String impId, PaymentHistory paymentHistory, String reason) {
        if (!cancelIamportPayment(impId, reason)) {
            throw new IllegalStateException("[Cancel Payment] 결제 취소가 실패했습니다.");
        }
        paymentHistory.cancel();
    }

    public boolean cancelIamportPayment(String impId, String reason) {
        try {
            CancelData cancelData = new CancelData(impId, true);
            cancelData.setReason(reason);
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);
            if (response != null && response.getResponse().getStatus().equals("cancelled")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new CancelPaymentException("[Iamport] 결제 취소 중 예외가 발생했습니다.", e);
        }
    }

    public boolean validatePaymentAmount(String impId, Long amount) {
        try {
            IamportResponse<Payment> payment = iamportClient.paymentByImpUid(impId);
            Long paidAmount = payment.getResponse().getAmount().longValue();
            if (paidAmount.equals(amount)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new PaymentValidationException("결제 사후 검증 중 예외가 발생했습니다.", e);
        }
    }
}
