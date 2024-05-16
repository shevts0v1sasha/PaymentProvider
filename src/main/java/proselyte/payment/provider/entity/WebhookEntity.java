package proselyte.payment.provider.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("webhooks")
public class WebhookEntity {

    @Id
    private Long id;

    @Column("transaction_uid")
    private String transactionUid;

    @Transient
    @ToString.Exclude
    private TransactionEntity transaction;

    @Column("invocation_date")
    private LocalDateTime invocationDate;

    @Column("message")
    private String message;

    @Column("transaction_status")
    private TransactionStatus transactionStatus;

    @Column("attempt_number")
    private Integer attemptNumber;

    private WebhookStatus webhookStatus;
}
