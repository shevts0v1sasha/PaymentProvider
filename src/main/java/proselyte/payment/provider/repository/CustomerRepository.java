package proselyte.payment.provider.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import proselyte.payment.provider.entity.customer.CustomerEntity;

public interface CustomerRepository extends R2dbcRepository<CustomerEntity, Long> {
}
