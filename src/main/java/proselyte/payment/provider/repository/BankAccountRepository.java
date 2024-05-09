package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import proselyte.payment.provider.entity.bankAccount.BankAccountEntity;
import reactor.core.publisher.Flux;

import java.util.List;

public interface BankAccountRepository extends R2dbcRepository<BankAccountEntity, Long> {

//    @Query("SELECT b FROM bank_accounts b WHERE b.id IN (:ids)")
//    Flux<BankAccountEntity> findAllById(@Param("ids") List<String> ids);
}
