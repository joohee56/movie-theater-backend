package mt.movie_theater.api.payment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mt.movie_theater.domain.payment.Currency;

@Getter
@NoArgsConstructor
public class ConfirmBookingRequest {

    @NotNull(message = "예매 ID는 필수 입력값입니다.")
    private Long bookingId;

    @NotBlank(message = "impId는 필수 입력값입니다.")
    private String impId;

    @NotNull(message = "결제금액은 필수 입력값입니다.")
    private Long amount;

    @NotBlank(message = "예매 번호는 필수 입력값입니다.")
    private String bookingNumber;

    @NotNull(message = "결제 시각은 필수 입력값입니다.")
    private Long payTime;

    @NotBlank(message = "결제 수단은 필수 입력값입니다.")
    private String payMethod;

    @NotNull(message = "결제 통화는 필수 입력값입니다.")
    private Currency currency;

    @Builder
    public ConfirmBookingRequest(Long bookingId, String impId, Long amount, String bookingNumber, Long payTime,
                                 String payMethod, Currency currency) {
        this.bookingId = bookingId;
        this.impId = impId;
        this.amount = amount;
        this.bookingNumber = bookingNumber;
        this.payTime = payTime;
        this.payMethod = payMethod;
        this.currency = currency;
    }
}
