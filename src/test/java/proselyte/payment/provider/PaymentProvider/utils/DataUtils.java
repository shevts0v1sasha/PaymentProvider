package proselyte.payment.provider.PaymentProvider.utils;

import proselyte.payment.provider.dto.CreateTransactionRequest;
import proselyte.payment.provider.dto.CustomerDto;
import proselyte.payment.provider.dto.MerchantDto;
import proselyte.payment.provider.dto.PaymentCardDataDto;
import proselyte.payment.provider.entity.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class DataUtils {

    private DataUtils() {
    }

    private static Long BANK_ACCOUNT_ID = 1L;

    public static PaymentCardEntity getPaymentCardTransient(Long customerId) {
        return PaymentCardEntity.builder()
                .cardNumber("4102778822334893")
                .expireDate(LocalDateTime.now().plusMonths(12))
                .cvv("566")
                .customerId(customerId)
                .build();
    }

    public static PaymentCardEntity getPaymentCardPersisted() {
        return getPaymentCardTransient(1L)
                .toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
    }

    public static CustomerEntity getCustomerTransient() {
        return CustomerEntity.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .country("Russia")
                .dateOfBirth(LocalDate.of(1996, 11, 15))
                .build();
    }

    public static CustomerEntity getCustomerPersisted() {
        return getCustomerTransient()
                .toBuilder()
                .id(1L)
                .build();
    }

    public static MerchantEntity getMerchantTransient() {
        return MerchantEntity.builder()
                .firstName("Nikolay")
                .lastName("Petrov")
                .country("Russia")
                .dateOfBirth(LocalDate.of(2000, 2, 16))
                .build();
    }

    public static MerchantEntity getMerchantPersisted() {
        return getMerchantTransient().toBuilder()
                .id("MERCHANT_ID")
                .build();
    }

    public static BankAccountEntity getBankAccountPersisted() {
        return BankAccountEntity.builder()

                .build();
    }

    public static CreateTransactionRequest createTransactionRequest(double amount) {
        PaymentCardDataDto cardDataDto = new PaymentCardDataDto("7ddd5096-bc8b-4ec9-a23c-5bd239dd893e", "4102778822334893", null, "566");
        CustomerDto customerDto = new CustomerDto(1L, "Ivan", "Ivanov", "Russia", LocalDate.of(1996, 11, 15), List.of(cardDataDto));
        MerchantDto merchantDto = new MerchantDto("e15d2c9c-b9b7-44ce-9964-b28208edc8fa", "Alexandr", "Shevtsov", "Russia", LocalDate.of(2000, 2, 16));
        return new CreateTransactionRequest(amount, CurrencyType.RUB, cardDataDto, "Rus",
                "http://localhost:8787/api/v1/webhooks", customerDto, merchantDto);
    }

//    public static Flux<BankAccountEntity> getBankAccountsByOwnerPersisted(String merchantId, double balance, CurrencyType currencyType) {
//        return Flux.just(BankAccountEntity.builder()
//                .id(BANK_ACCOUNT_ID++)
//                .currency(currencyType)
//                .balance(balance)
//                .ownerUid(merchantId)
//                .build());
//    }
}
