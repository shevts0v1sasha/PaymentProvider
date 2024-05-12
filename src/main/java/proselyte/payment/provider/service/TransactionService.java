package proselyte.payment.provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proselyte.payment.provider.dto.PaymentCardDataDto;
import proselyte.payment.provider.entity.*;
import proselyte.payment.provider.exception.ApiException;
import proselyte.payment.provider.repository.*;
import proselyte.payment.provider.rest.request.CreateTransactionRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final WebhookService webhookService;

    public Mono<TransactionEntity> createTopUpTransaction(CreateTransactionRequest request) {
        PaymentCardDataDto paymentCardDataDto = request.cardData();

        Mono<BankAccountEntity> fromBankAccountMono = findBankAccountByPaymentCard(paymentCardDataDto.cardNumber());
        Mono<BankAccountEntity> toBankAccountMono = findBankAccountByMerchantIdAndCurrency(request.merchant().id(), request.currency());

        return createTransaction(request, fromBankAccountMono, toBankAccountMono);
    }

    public Mono<TransactionEntity> createPayoutTransaction(CreateTransactionRequest request) {
        PaymentCardDataDto paymentCardDataDto = request.cardData();

        Mono<BankAccountEntity> fromBankAccountMono = findBankAccountByMerchantIdAndCurrency(request.merchant().id(), request.currency());
        Mono<BankAccountEntity> toBankAccountMono = findBankAccountByPaymentCard(paymentCardDataDto.cardNumber());
        return createTransaction(request, fromBankAccountMono, toBankAccountMono);
    }

    private Mono<BankAccountEntity> findBankAccountByMerchantIdAndCurrency(String merchantId, CurrencyType currencyType) {
        return merchantRepository.findById(merchantId)
                .flatMapMany(merchantEntity -> bankAccountRepository.findAllByOwnerUid(merchantEntity.getId()))
                .filter(bankAccountEntity -> bankAccountEntity.getCurrency() == currencyType)
                .next()
                .switchIfEmpty(Mono.error(new ApiException("BANK_ACCOUNT_BY_MERCHANT_NOT_FOUND")));
    }

    private Mono<BankAccountEntity> findBankAccountByPaymentCard(String cardNumber) {
        return paymentCardRepository.findByCardNumber(cardNumber)
                .flatMapMany(paymentCardEntity -> bankAccountRepository.findAllByOwnerUid(paymentCardEntity.getId()))
                .next()
                .switchIfEmpty(Mono.error(new ApiException("BANK_ACCOUNT_BY_CARD_NOT_FOUND")));
    }

    public Flux<TransactionEntity> getAllInProgressTransactions() {
        return transactionRepository.findAll()
                .filter(transactionEntity -> transactionEntity.getTransactionStatus() == TransactionStatus.IN_PROGRESS);
    }

    public Mono<TransactionEntity> saveOrUpdateTransaction(TransactionEntity transaction) {
        return transactionRepository.save(transaction);
    }

    private Mono<TransactionEntity> createTransaction(CreateTransactionRequest request,
                                                      Mono<BankAccountEntity> fromBankAccountMono,
                                                      Mono<BankAccountEntity> toBankAccountMono) {
        CurrencyType currency = request.currency();
        double amount = request.amount();
        return Mono.zip(fromBankAccountMono, toBankAccountMono)
                .flatMap(tuples -> {
                     BankAccountEntity fromBankAccount = tuples.getT1();
                     BankAccountEntity toBankAccount = tuples.getT2();

                     if (fromBankAccount.getCurrency() != currency || toBankAccount.getCurrency() != currency) {
                         return Mono.error(new ApiException("SOME_OF_CURRENCY_DOES_NOT_MATCH"));
                     }

                    double merchantsBalance = fromBankAccount.getBalance();
                    if (merchantsBalance >= amount) {
                        TransactionEntity transaction = TransactionEntity.builder()
                                .transactionStatus(TransactionStatus.IN_PROGRESS)
                                .transactionType(TransactionType.TOP_UP)
                                .fromBankAccountId(fromBankAccount.getId())
                                .toBankAccountId(toBankAccount.getId())
                                .amount(amount)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .language(request.language())
                                .build();
                        fromBankAccount.setBalance(merchantsBalance - amount);

                        return bankAccountRepository.save(fromBankAccount)
                                .flatMap(bankAccountEntity -> transactionRepository.save(transaction))
                                .doOnSuccess(savedTransaction -> webhookService.saveWebhook(savedTransaction, request.notificationUrl()).subscribe());

                    } else {
                        return Mono.error(new ApiException("NOT_ENOUGH_MONEY"));
                    }

                });
    }

}
