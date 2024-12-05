package mt.movie_theater.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.Optional;
import mt.movie_theater.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class PaymentHistoryRepositoryTest extends IntegrationTestSupport {
    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @DisplayName("결제번호(ImpId)로 결제내역을 조회한다.")
    @Test
    void findByImpId() {
        //given
        PaymentHistory paymentHistory1 = createPaymentHistory("001");
        PaymentHistory paymentHistory2 = createPaymentHistory("002");

        //when
        Optional<PaymentHistory> paymentHistory = paymentHistoryRepository.findByImpId(paymentHistory1.getImpId());

        //then
        assertThat(paymentHistory).isPresent();
        assertThat(paymentHistory.get().getImpId()).isEqualTo(paymentHistory.get().getImpId());
    }

    private PaymentHistory createPaymentHistory(String impId) {
        PaymentHistory paymentHistory = PaymentHistory.builder()
                .impId(impId)
                .build();

        return paymentHistoryRepository.save(paymentHistory);
    }
}