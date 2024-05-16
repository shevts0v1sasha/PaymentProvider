package proselyte.payment.provider.PaymentProvider.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import proselyte.payment.provider.PaymentProvider.utils.DataUtils;
import proselyte.payment.provider.entity.MerchantEntity;
import proselyte.payment.provider.repository.MerchantRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
public class MerchantRepositoryTests {

    @Autowired
    private MerchantRepository merchantRepository;

    @BeforeEach
    public void setUp() {
        merchantRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test save merchant functionality")
    public void givenMerchantObject_whenSave_thenMerchantIsCreated() {
        //given
        MerchantEntity merchantTransient = DataUtils.getMerchantTransient();
        //when
        MerchantEntity savedMerchant = merchantRepository.save(merchantTransient).block();
        //then
        assertThat(savedMerchant).isNotNull();
        assertThat(savedMerchant.getId()).isNotNull();
    }
}
