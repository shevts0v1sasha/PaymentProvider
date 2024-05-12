package proselyte.payment.provider.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proselyte.payment.provider.rest.request.CreateTransactionRequest;
import proselyte.payment.provider.rest.request.CreateTransactionResponse;
import proselyte.payment.provider.service.TransactionService;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentRestControllerV1 {

    private final TransactionService transactionService;

    @PostMapping("/topups")
    public Mono<CreateTransactionResponse> createTopUpTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionService.createTopUpTransaction(request)
                .map(transactionEntity -> new CreateTransactionResponse(transactionEntity.getId(), transactionEntity.getTransactionStatus(), "OK"));
    }

    @PostMapping("/payouts")
    public Mono<CreateTransactionResponse> createPayoutTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionService.createPayoutTransaction(request)
                .map(transactionEntity -> new CreateTransactionResponse(transactionEntity.getId(), transactionEntity.getTransactionStatus(), "OK"));
    }
}
