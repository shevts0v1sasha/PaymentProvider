package proselyte.payment.provider.dto;

import lombok.Builder;
import proselyte.payment.provider.entity.TransactionStatus;
import proselyte.payment.provider.entity.WebhookStatus;

import java.time.LocalDateTime;

@Builder
public record WebhookDto(String transactionUid,
                         LocalDateTime invocationDate,
                         String message,
                         TransactionStatus transactionStatus,
                         Integer attemptNumber,
                         WebhookStatus webhookStatus) {
}
