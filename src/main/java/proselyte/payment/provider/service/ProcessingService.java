package proselyte.payment.provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proselyte.payment.provider.entity.BankAccountEntity;
import proselyte.payment.provider.entity.TransactionEntity;
import proselyte.payment.provider.entity.TransactionStatus;
import proselyte.payment.provider.exception.NotFoundException;
import proselyte.payment.provider.repository.BankAccountRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessingService {

    private final static long TEN_SECONDS_IN_MILLIS = 10_000;

    private final TransactionService transactionService;
    private final WebhookService webhookService;
    private final BankAccountRepository bankAccountRepository;

    @Scheduled(initialDelay = TEN_SECONDS_IN_MILLIS, fixedRate = TEN_SECONDS_IN_MILLIS)
    public void runProcessing() {
        processTransactions().subscribe();
    }

    @Transactional
    public Flux<TransactionEntity> processTransactions() {
        return transactionService.getAllInProgressTransactions()
                .flatMap(transaction -> {
                    TransactionStatus newStatus = randomNewTransactionStatus();
                    log.info("Transaction with id: {} now has new status: {}", transaction.getId(), newStatus);
                    transaction.setTransactionStatus(newStatus);

                    return Mono.zip(
                                    bankAccountRepository.findById(transaction.getFromBankAccountId()),
                                    bankAccountRepository.findById(transaction.getToBankAccountId())
                            )
                            .flatMap(tuple -> {
                                BankAccountEntity fromBankAccount = tuple.getT1();
                                BankAccountEntity toBankAccount = tuple.getT2();

                                Mono<BankAccountEntity> savedBankAccount;

                                switch (newStatus) {
                                    case SUCCESS -> {
                                        double balance = toBankAccount.getBalance();
                                        toBankAccount.setBalance(balance + transaction.getAmount());
                                        savedBankAccount = bankAccountRepository.save(toBankAccount);
                                    }
                                    case FAILED -> {
                                        double balance = fromBankAccount.getBalance();
                                        fromBankAccount.setBalance(balance + transaction.getAmount());
                                        savedBankAccount = bankAccountRepository.save(fromBankAccount);
                                    }
                                    default -> {
                                        return Mono.error(new RuntimeException("UNKNOWN_TRANSACTION_STATUS"));
                                    }
                                }

                                return savedBankAccount
                                        .flatMap(bankAccountEntity -> transactionService.saveOrUpdateTransaction(transaction));
                            })
                            .switchIfEmpty(Mono.error(new NotFoundException("Some of bank account not found")));
                });
    }

    /**
     * 0.9 probability - TransactionStatus.SUCCESS
     * 0.1 probability - TransactionStatus.FAILED
     */
    private TransactionStatus randomNewTransactionStatus() {
        double random = ThreadLocalRandom.current().nextDouble();
        return random < 0.9 ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;
    }
}
