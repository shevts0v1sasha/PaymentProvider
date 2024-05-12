package proselyte.payment.provider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("payment_cards")
public class PaymentCardEntity {

    @Id
    private String id;

    @Transient
    private BankAccountEntity bankAccount;

    @Column("card_number")
    private String cardNumber;

    @Column("expire_date")
    private LocalDateTime expireDate;

    @Column("cvv")
    private String cvv;

    @Column("customer_id")
    private Long customerId;

    @Transient
    private CustomerEntity customer;
}
