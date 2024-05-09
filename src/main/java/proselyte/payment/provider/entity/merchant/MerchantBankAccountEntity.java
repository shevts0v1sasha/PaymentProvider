package proselyte.payment.provider.entity.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("merchants_bank_accounts")
public class MerchantBankAccountEntity {

    @Id
    private Long id;

    @Column("merchant_id")
    private Long merchantId;

    @Column("bank_account_id")
    private Long bankAccountId;
}
