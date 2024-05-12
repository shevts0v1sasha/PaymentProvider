package proselyte.payment.provider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MerchantDto(String id,
                          String firstName,
                          String lastName,
                          String country,
                          LocalDate dateOfBirth) {
}
