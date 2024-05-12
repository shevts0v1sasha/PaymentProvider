package proselyte.payment.provider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import proselyte.payment.provider.entity.CurrencyType;
import proselyte.payment.provider.entity.TransactionStatus;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionDto(String transactionId,
                             double amount,
                             BankAccountDto fromBankAccount,
                             BankAccountDto toBankAccount,
                             CurrencyType currency,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt,
                             PaymentCardDataDto cardData,
                             String language,
                             CustomerDto customer,
                             TransactionStatus status) {
}
