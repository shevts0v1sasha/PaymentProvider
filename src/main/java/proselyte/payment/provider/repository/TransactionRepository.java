package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.transaction.TransactionEntity;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, String> {
}
