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
@Table("webhook_invocations")
@ToString(of = {"id", "webhookId", "invocationDate", "message", "transactionStatus"})
public class WebhookInvocationEntity {

    @Id
    private Long id;

    @Column("webhook_id")
    private Long webhookId;

    @Transient
    private WebhookEntity webhook;

    @Column("invocation_date")
    private LocalDateTime invocationDate;


    @Column("message")
    private String message;

    @Column("transaction_status")
    private TransactionStatus transactionStatus;
}
