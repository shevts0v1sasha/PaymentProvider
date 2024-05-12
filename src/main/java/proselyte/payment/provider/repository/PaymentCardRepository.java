package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.PaymentCardEntity;
import reactor.core.publisher.Mono;

public interface PaymentCardRepository extends R2dbcRepository<PaymentCardEntity, String> {
    Mono<PaymentCardEntity> findByCardNumber(String cardNumber);
}
