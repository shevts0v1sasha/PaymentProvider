package proselyte.payment.provider.rest.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import proselyte.payment.provider.dto.CustomerDto;
import proselyte.payment.provider.dto.MerchantDto;
import proselyte.payment.provider.dto.PaymentCardDataDto;
import proselyte.payment.provider.entity.CurrencyType;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTransactionRequest(double amount,
                                       CurrencyType currency,
                                       PaymentCardDataDto cardData,
                                       String language,
                                       String notificationUrl,
                                       CustomerDto customer,
                                       MerchantDto merchant
                                 ) {
}
