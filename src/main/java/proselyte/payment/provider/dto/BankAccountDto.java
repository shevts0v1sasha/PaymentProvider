package proselyte.payment.provider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import proselyte.payment.provider.entity.CurrencyType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BankAccountDto(String id,
                             CurrencyType currency) {
}
