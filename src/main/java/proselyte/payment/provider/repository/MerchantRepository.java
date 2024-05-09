package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.merchant.MerchantEntity;

public interface MerchantRepository extends R2dbcRepository<MerchantEntity, Long> {
}
