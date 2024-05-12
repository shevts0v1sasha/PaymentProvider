package proselyte.payment.provider.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("webhooks")
@ToString(of = {"id", "transactionUid", "notificationUrl"})
public class WebhookEntity {

    @Id
    private Long id;

    @Column("transaction_uid")
    private String transactionUid;

    @Transient
    private TransactionEntity transaction;

    @Column("notification_url")
    private String notificationUrl;
}
