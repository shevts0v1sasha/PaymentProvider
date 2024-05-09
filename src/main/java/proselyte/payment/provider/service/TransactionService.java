package proselyte.payment.provider.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proselyte.payment.provider.dto.PaymentCardDataDto;
import proselyte.payment.provider.entity.bankAccount.BankAccountEntity;
import proselyte.payment.provider.entity.bankAccount.CurrencyType;
import proselyte.payment.provider.entity.merchant.MerchantBankAccountEntity;
import proselyte.payment.provider.entity.transaction.TransactionEntity;
import proselyte.payment.provider.entity.transaction.TransactionStatus;
import proselyte.payment.provider.entity.transaction.TransactionType;
import proselyte.payment.provider.exception.ApiException;
import proselyte.payment.provider.repository.*;
import proselyte.payment.provider.rest.request.CreateTopUpRequest;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final MerchantBankAccountRepository merchantBankAccountRepository;

    public Mono<TransactionEntity> createTopUpTransaction(CreateTopUpRequest request) {
        PaymentCardDataDto paymentCardDataDto = request.cardData();
        double amount = request.amount();

        return Mono.zip(
                        findBankAccountByMerchantIdAndCurrency(request.merchant().id(), request.currency()),
                        findBankAccountByPaymentCard(paymentCardDataDto.cardNumber())
                )
                .flatMap(tuples -> {
                    BankAccountEntity merchantBankAccount = tuples.getT1();
                    BankAccountEntity customerBankAccount = tuples.getT2();

                    double customersBalance = customerBankAccount.getBalance();
                    if (customersBalance >= amount) {
                        TransactionEntity transaction = TransactionEntity.builder()
                                .transactionStatus(TransactionStatus.IN_PROGRESS)
                                .transactionType(TransactionType.TOP_UP)
                                .fromBankAccountId(customerBankAccount.getId())
                                .toBankAccountId(merchantBankAccount.getId())
                                .amount(amount)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .language(request.language())
                                .build();
                        customerBankAccount.setBalance(customersBalance - amount);

                        return bankAccountRepository.save(customerBankAccount)
                                .flatMap(bankAccountEntity -> transactionRepository.save(transaction));

                    } else {
                        return Mono.error(new ApiException("NOT_ENOUGH_MONEY"));
                    }
                });
    }

    private Mono<BankAccountEntity> findBankAccountByMerchantIdAndCurrency(long merchantId, CurrencyType currencyType) {
        return merchantRepository.findById(merchantId)
                .flatMap(merchantEntity -> merchantBankAccountRepository.findAllByMerchantId(merchantEntity.getId()).collectList())
                .flatMapMany(merchantBankAccountEntities -> {
                    List<Long> merchantBankAccountIds = merchantBankAccountEntities.stream()
                            .map(MerchantBankAccountEntity::getBankAccountId)
                            .toList();

                    return bankAccountRepository.findAllById(merchantBankAccountIds);
                })
                .filter(bankAccountEntity -> bankAccountEntity.getCurrency() == currencyType)
                .next();
    }

    private Mono<BankAccountEntity> findBankAccountByPaymentCard(String cardNumber) {
        return paymentCardRepository.findByCardNumber(cardNumber)
                .flatMap(paymentCardEntity -> bankAccountRepository.findById(paymentCardEntity.getBankAccountId()));
    }
}
