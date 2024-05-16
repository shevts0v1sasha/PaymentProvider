package proselyte.payment.provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import proselyte.payment.provider.dto.WebhookClientResponse;
import proselyte.payment.provider.dto.WebhookDto;
import proselyte.payment.provider.entity.TransactionEntity;
import proselyte.payment.provider.entity.TransactionStatus;
import proselyte.payment.provider.entity.WebhookEntity;
import proselyte.payment.provider.entity.WebhookStatus;
import proselyte.payment.provider.exception.WebhookExhaustedRetriesException;
import proselyte.payment.provider.exception.WebhookNotReceivedException;
import proselyte.payment.provider.mapper.WebhookMapper;
import proselyte.payment.provider.repository.WebhookRepository;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final WebhookMapper webhookMapper;

    public void registerWebhookInvocation(TransactionEntity transaction, String message, TransactionStatus transactionStatus) {
        log.info("registerWebhookInvocation");
        invokeWebhook(transaction, message, transactionStatus)
                .subscribe();

    }

    public Mono<WebhookEntity> invokeWebhook(TransactionEntity transaction, String message, TransactionStatus transactionStatus) {
        AtomicInteger attempt = new AtomicInteger(1);
        WebhookDto webhookDto = new WebhookDto(transaction.getId(), LocalDateTime.now(), message, transactionStatus, attempt.get(), null);

        return WebClient.create()
                .post()
                .uri(transaction.getNotificationUrl())
                .bodyValue(webhookDto)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        log.info("IN invokeWebhook webhook successfully reached the recipient. Transaction id: {}, status: {}, message: {}, attempt: {}",
                                transaction.getNotificationUrl(),
                                transactionStatus,
                                message,
                                attempt.get());
                        return clientResponse.bodyToMono(WebhookClientResponse.class);
                    } else {
                        log.error("Webhook couldn't reach recipient url: {}, attempt: {}, status: {}", transaction.getNotificationUrl(), attempt, clientResponse.statusCode());
                        return Mono.error(new WebhookNotReceivedException("Webhook not received"));
                    }
                })
                .flatMap(response -> buildWebhook(transaction.getId(), attempt.get(), transactionStatus, message, WebhookStatus.RECEIVED))
                .onErrorResume(throwable -> buildWebhook(transaction.getId(), attempt.get(), transactionStatus, message, WebhookStatus.FAILED))
                .flatMap(webhookEntity -> webhookRepository.save(webhookEntity)
                        .flatMap(w -> {
                            attempt.getAndIncrement();
                            if (w.getWebhookStatus() == WebhookStatus.FAILED) {
                                return Mono.error(new WebhookNotReceivedException("Webhook couldn't reach url: %s".formatted(transaction.getNotificationUrl())));
                            }
                            return Mono.just(w);
                        })
                        .doOnSuccess(webhook -> log.info("Webhook invocation saved. WebhookInvocation: {}", webhook))
                        .doOnError(throwable -> log.error("IN invokeWebhook exception occurred. Message: {}", throwable.getMessage())))
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(5))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new WebhookExhaustedRetriesException("Webhook retries exhausted %d/%d"
                                .formatted(retrySignal.totalRetries(), retrySignal.totalRetries()))))
                .doOnError(throwable -> log.error("In webhook send exception occurred: {}", throwable.getMessage()))
                .onErrorResume(throwable -> Mono.empty());
    }

    private Mono<WebhookEntity> buildWebhook(String transactionUid, int attempt, TransactionStatus transactionStatus, String message, WebhookStatus webhookStatus) {
        WebhookEntity webhook = WebhookEntity.builder()
                .transactionUid(transactionUid)
                .invocationDate(LocalDateTime.now())
                .message(message)
                .transactionStatus(transactionStatus)
                .attemptNumber(attempt)
                .build();
        if (webhookStatus == WebhookStatus.RECEIVED) {
            return Mono.just(webhook.toBuilder()
                    .webhookStatus(WebhookStatus.RECEIVED)
                    .build());
        } else {
            return Mono.just(webhook.toBuilder()
                    .webhookStatus(WebhookStatus.FAILED)
                    .build());
        }
    }
}
