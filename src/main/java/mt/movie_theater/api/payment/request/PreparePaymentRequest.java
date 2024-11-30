package mt.movie_theater.api.payment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PreparePaymentRequest {

    @NotBlank(message = "예매번호는 필수 입력값입니다.")
    private String bookingNumber;

    @NotNull(message = "금액은 필수 입력값입니다.")
    private Long amount;

    @Builder
    public PreparePaymentRequest(String bookingNumber, Long amount) {
        this.bookingNumber = bookingNumber;
        this.amount = amount;
    }
}
