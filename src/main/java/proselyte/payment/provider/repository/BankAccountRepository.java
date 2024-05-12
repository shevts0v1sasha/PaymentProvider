package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.BankAccountEntity;
import reactor.core.publisher.Flux;

public interface BankAccountRepository extends R2dbcRepository<BankAccountEntity, Long> {

//    @Query("SELECT b FROM bank_accounts b WHERE b.id IN (:ids)")
//    Flux<BankAccountEntity> findAllById(@Param("ids") List<String> ids);

    Flux<BankAccountEntity> findAllByOwnerUid(String ownerUid);
}
