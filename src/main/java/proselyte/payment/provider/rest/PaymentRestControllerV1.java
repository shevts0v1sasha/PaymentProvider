package proselyte.payment.provider.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proselyte.payment.provider.rest.request.CreateTopUpRequest;
import proselyte.payment.provider.rest.request.CreateTopUpResponse;
import proselyte.payment.provider.service.TransactionService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentRestControllerV1 {

    private final TransactionService transactionService;

    @PostMapping("/topups")
    public Mono<CreateTopUpResponse> createTopUpTransaction(@RequestBody CreateTopUpRequest request) {
        return transactionService.createTopUpTransaction(request)
                .map(transactionEntity -> new CreateTopUpResponse(transactionEntity.getId(), transactionEntity.getTransactionStatus(), "OK"));
    }
}
