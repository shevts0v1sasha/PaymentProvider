package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.WebhookEntity;
import reactor.core.publisher.Mono;

public interface WebhookRepository extends R2dbcRepository<WebhookEntity, Long> {
    Mono<WebhookEntity> findByTransactionUid(String transactionUid);

//    @Query("SELECT w FROM webhooks w WHERE w.transaction_uid = ?1 ORDER BY w.attempt_number DESC LIMIT 1")
//    Mono<WebhookEntity> findLastWebhookByTransactionId(String transactionUid);

    Mono<WebhookEntity> findFirstByTransactionUidOrderByAttemptNumberDesc(String transactionUid);
}
