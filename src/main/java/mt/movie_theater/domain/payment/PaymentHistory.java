package mt.movie_theater.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.api.payment.request.ConfirmBookingRequest;
import mt.movie_theater.domain.user.User;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String impId;

    private Long amount;

    private LocalDateTime payTime;

    private String payMethod;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    @Builder
    private PaymentHistory(User user, String impId, Long amount, LocalDateTime payTime, String payMethod, Currency currency, PayStatus payStatus) {
        this.user = user;
        this.impId = impId;
        this.amount = amount;
        this.payTime = payTime;
        this.payMethod = payMethod;
        this.currency = currency;
        this.payStatus = payStatus;
    }

    public static PaymentHistory create(ConfirmBookingRequest request, User user) {
        return PaymentHistory.builder()
                .user(user)
                .impId(request.getImpId())
                .amount(request.getAmount())
                .payTime(convertUnixTimestampToLocalDateTime(request.getPayTime()))
                .payMethod(request.getPayMethod())
                .currency(request.getCurrency())
                .payStatus(PayStatus.COMPLETED)
                .build();
    }

    private static LocalDateTime convertUnixTimestampToLocalDateTime(Long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
    }

    public void cancel() {
        this.payStatus = PayStatus.CANCELED;
    }

    public void fail() {
        this.payStatus = PayStatus.FAILED;
    }
}
