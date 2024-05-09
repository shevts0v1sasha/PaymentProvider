package proselyte.payment.provider.rest.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import proselyte.payment.provider.entity.transaction.TransactionStatus;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTopUpResponse(String transactionId,
                                  TransactionStatus status,
                                  String message) {
}
