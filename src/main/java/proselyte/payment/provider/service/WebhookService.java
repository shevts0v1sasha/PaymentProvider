package proselyte.payment.provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proselyte.payment.provider.entity.TransactionEntity;
import proselyte.payment.provider.entity.TransactionStatus;
import proselyte.payment.provider.entity.WebhookEntity;
import proselyte.payment.provider.entity.WebhookInvocationEntity;
import proselyte.payment.provider.repository.WebhookInvocationRepository;
import proselyte.payment.provider.repository.WebhookRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final WebhookInvocationRepository webhookInvocationRepository;

    public Mono<WebhookEntity> saveWebhook(TransactionEntity transaction, String notificationUrl) {
        WebhookEntity webhook = WebhookEntity.builder()
                .transactionUid(transaction.getId())
                .notificationUrl(notificationUrl)
                .build();

        return webhookRepository.save(webhook)
                .doOnSuccess(savedWebhook -> log.info("Successfully saved webhook. Transaction id: {}, notificationUrl: {}",
                        transaction.getId(), notificationUrl))
                .doOnError(throwable -> log.error("IN saveWebhook exception occurred. Message: {}", throwable.getMessage()));
    }

    public Mono<WebhookInvocationEntity> invokeWebhook(WebhookEntity webhook, String message, TransactionStatus transactionStatus) {
        WebhookInvocationEntity webhookInvocation = WebhookInvocationEntity.builder()
                .webhookId(webhook.getId())
                .invocationDate(LocalDateTime.now())
                .message(message)
                .transactionStatus(transactionStatus)
                .build();

        log.info("IN invokeWebhook webhook invoked. Transaction id: {}, status: {}, webhook id: {} message: {}",
                webhook.getTransactionUid(),
                transactionStatus,
                webhook.getId(),
                message);

        // invocation

        return webhookInvocationRepository.save(webhookInvocation)
                .doOnSuccess(webhookInvocationEntity -> log.info("Webhook invocation saved. WebhookInvocation: {}", webhookInvocationEntity))
                .doOnError(throwable -> log.error("IN invokeWebhook exception occurred. Message: {}", throwable.getMessage()));
    }

    public Mono<WebhookEntity> findWebhookByTransactionUid(String transactionUid) {
        return webhookRepository.findByTransactionUid(transactionUid);
    }
}
