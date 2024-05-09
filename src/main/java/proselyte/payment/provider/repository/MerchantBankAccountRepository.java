package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.merchant.MerchantBankAccountEntity;
import reactor.core.publisher.Flux;

public interface MerchantBankAccountRepository extends R2dbcRepository<MerchantBankAccountEntity, Long> {
    Flux<MerchantBankAccountEntity> findAllByMerchantId(long merchantId);
}
