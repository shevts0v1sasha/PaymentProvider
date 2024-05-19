package proselyte.payment.provider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import proselyte.payment.provider.entity.CurrencyType;
import proselyte.payment.provider.entity.TransactionEntity;
import proselyte.payment.provider.entity.TransactionStatus;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionDto(String id,
                             double amount,
                             BankAccountDto fromBankAccount,
                             BankAccountDto toBankAccount,
                             CurrencyType currency,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt,
                             String language,
                             TransactionStatus transactionStatus) {

    public static TransactionDto fromEntity(TransactionEntity entity) {
        return new TransactionDto(
                entity.getId(),
                entity.getAmount(),
                new BankAccountDto(entity.getFromBankAccountId(), entity.getFromBankAccount().getCurrency()),
                new BankAccountDto(entity.getToBankAccountId(), entity.getToBankAccount().getCurrency()),
                entity.getToBankAccount().getCurrency(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLanguage(),
                entity.getTransactionStatus()
        );
    }
}
