package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.WebhookInvocationEntity;

public interface WebhookInvocationRepository extends R2dbcRepository<WebhookInvocationEntity, Long> {
}
