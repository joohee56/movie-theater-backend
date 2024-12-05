package mt.movie_theater.api.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mt.movie_theater.api.apiResponse.ApiResponse;
import mt.movie_theater.api.payment.request.PreparePaymentRequest;
import mt.movie_theater.api.payment.service.PaymentHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    @PostMapping("/prepare")
    public ApiResponse<String> preparePayment(@Valid @RequestBody PreparePaymentRequest request) {
        boolean result  = paymentHistoryService.preparePayment(request.getBookingNumber(), request.getAmount());
        if (result) {
            return ApiResponse.ok("결제 사전 검증을 등록했습니다.");
        }
        return ApiResponse.of(HttpStatus.BAD_REQUEST, "결제 사전 검증 등록을 실패했습니다.", null);
    }
}
