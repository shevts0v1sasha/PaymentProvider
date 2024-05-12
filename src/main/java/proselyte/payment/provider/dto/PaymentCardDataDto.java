package proselyte.payment.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaymentCardDataDto(String id,
                                 String cardNumber,
                                 @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                                 String expDate,
                                 String cvv) {
}
