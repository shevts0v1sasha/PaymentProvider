package proselyte.payment.provider.entity.bankAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("bank_accounts")
public class BankAccountEntity {

    @Id
    private Long id;

    @Column("currency")
    private CurrencyType currency;

    @Column("balance")
    private double balance;
}
