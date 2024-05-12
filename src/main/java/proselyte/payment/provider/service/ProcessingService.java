package proselyte.payment.provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import proselyte.payment.provider.entity.BankAccountEntity;
import proselyte.payment.provider.entity.TransactionEntity;
import proselyte.payment.provider.entity.TransactionStatus;
import proselyte.payment.provider.entity.WebhookEntity;
import proselyte.payment.provider.repository.BankAccountRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessingService {

    private final static long THIRTY_SECONDS_IN_MILLIS = 30_000;

    private final TransactionService transactionService;
    private final WebhookService webhookService;
    private final BankAccountRepository bankAccountRepository;

    @Scheduled(initialDelay = THIRTY_SECONDS_IN_MILLIS, fixedRate = THIRTY_SECONDS_IN_MILLIS)
    private void runProcessing() {
        log.info("Start transaction processing method");
        List<TransactionEntity> transactions = transactionService.getAllInProgressTransactions()
                .collectList()
                .block();

        if (transactions != null) {
            for (TransactionEntity transaction : transactions) {
                double random = ThreadLocalRandom.current().nextDouble();
                TransactionStatus newStatus = random < 0.9 ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;
                log.info("Transaction with id: {} now has new status: {}", transaction.getId(), newStatus);
                transaction.setTransactionStatus(newStatus);

                String message = newStatus == TransactionStatus.SUCCESS ?
                        "OK" :
                        "FAILED";

                WebhookEntity webhookByTransactionUid = webhookService.findWebhookByTransactionUid(transaction.getId()).block();
                if (webhookByTransactionUid != null) {
                    webhookService.invokeWebhook(webhookByTransactionUid, message, newStatus).subscribe();
                } else {
                    log.error("Couldn't find webhook for transaction with id: {}", transaction.getId());
                }

                BankAccountEntity fromBankAccount = bankAccountRepository.findById(transaction.getFromBankAccountId()).block();
                BankAccountEntity toBankAccount = bankAccountRepository.findById(transaction.getToBankAccountId()).block();
                switch (newStatus) {
                    case SUCCESS -> {
                        double balance = toBankAccount.getBalance();
                        toBankAccount.setBalance(balance + transaction.getAmount());
                        bankAccountRepository.save(toBankAccount).subscribe();
                    }
                    case FAILED -> {
                        double balance = fromBankAccount.getBalance();
                        fromBankAccount.setBalance(balance + transaction.getAmount());
                        bankAccountRepository.save(fromBankAccount).subscribe();
                    }
                }

                transactionService.saveOrUpdateTransaction(transaction).subscribe();
            }
        }
    }
}
