package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.WebhookEntity;
import reactor.core.publisher.Mono;

public interface WebhookRepository extends R2dbcRepository<WebhookEntity, Long> {
    Mono<WebhookEntity> findByTransactionUid(String transactionUid);
}
