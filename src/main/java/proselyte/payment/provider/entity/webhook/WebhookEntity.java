package proselyte.payment.provider.entity.webhook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import proselyte.payment.provider.entity.transaction.TransactionEntity;
import proselyte.payment.provider.entity.transaction.TransactionStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("webhooks")
public class WebhookEntity {

    @Id
    private Long id;

    @Column("transaction_id")
    private Long transactionId;

    @Transient
    private TransactionEntity transaction;

    @Column("invocation_date")
    private LocalDateTime invocationDate;

    @Column("message")
    private String message;

    @Column("notification_url")
    private String notificationUrl;

    @Column("transaction_status")
    private TransactionStatus transactionStatus;
}
