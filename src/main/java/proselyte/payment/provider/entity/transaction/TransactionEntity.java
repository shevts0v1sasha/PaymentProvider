package proselyte.payment.provider.entity.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;
import proselyte.payment.provider.entity.bankAccount.BankAccountEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("transactions")
public class TransactionEntity implements Persistable<String> {

    @Id
    private String id;

    @Column("transaction_status")
    private TransactionStatus transactionStatus;

    @Column("transaction_type")
    private TransactionType transactionType;

    @Column("from_bank_account_id")
    private Long fromBankAccountId;

    @Transient
    private BankAccountEntity fromBankAccount;

    @Column("to_bank_account_id")
    private Long toBankAccountId;

    @Transient
    private BankAccountEntity toBankAccount;

    @Column("amount")
    private double amount;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("language")
    private String language;

    @Override
    public boolean isNew() {
        return !StringUtils.hasText(id);
    }
}
