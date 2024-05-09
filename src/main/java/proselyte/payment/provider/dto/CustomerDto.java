package proselyte.payment.provider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CustomerDto(Long id,
                          String firstName,
                          String lastName,
                          String country,
                          LocalDate dateOfBirth,
                          List<PaymentCardDataDto> paymentCards) {
}
