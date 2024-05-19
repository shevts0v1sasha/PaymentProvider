package proselyte.payment.provider.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import proselyte.payment.provider.dto.CreateTransactionRequest;
import proselyte.payment.provider.dto.CreateTransactionResponse;
import proselyte.payment.provider.dto.TransactionDto;
import proselyte.payment.provider.entity.TransactionEntity;
import proselyte.payment.provider.service.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentRestControllerV1 {

    private final TransactionService transactionService;

    @PostMapping("/topups")
    public Mono<CreateTransactionResponse> createTopUpTransaction(@RequestBody CreateTransactionRequest request) {
        log.info("Create top up request: {}", request);
        return transactionService.createTopUpTransaction(request)
                .doOnNext(transaction -> log.info("createTopUpTransaction returned: {}", transaction))
                .map(transactionEntity -> new CreateTransactionResponse(transactionEntity.getId(), transactionEntity.getTransactionStatus(), "OK"));
    }

    @PostMapping("/payouts")
    public Mono<CreateTransactionResponse> createPayoutTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionService.createPayoutTransaction(request)
                .map(transactionEntity -> new CreateTransactionResponse(transactionEntity.getId(), transactionEntity.getTransactionStatus(), "OK"));
    }

    @GetMapping("/transaction/list")
    public Flux<TransactionDto> getTransactionList(@RequestParam(value = "start_date", required = false) Long startDate,
                                                   @RequestParam(value = "end_date", required = false) Long endDate) {
        Flux<TransactionEntity> transactions = transactionService.getTransactionListByDates(startDate, endDate);
        return transactions
                .map(TransactionDto::fromEntity);
    }

    @GetMapping("/transaction/{transactionId}/details")
    public Mono<TransactionDto> getTransactionById(@PathVariable("transactionId") String transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(TransactionDto::fromEntity);

    }
}
