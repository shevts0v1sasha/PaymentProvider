package proselyte.payment.provider.PaymentProvider.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proselyte.payment.provider.PaymentProvider.utils.DataUtils;
import proselyte.payment.provider.dto.CreateTransactionRequest;
import proselyte.payment.provider.entity.*;
import proselyte.payment.provider.exception.ApiException;
import proselyte.payment.provider.exception.NotFoundException;
import proselyte.payment.provider.repository.BankAccountRepository;
import proselyte.payment.provider.repository.MerchantRepository;
import proselyte.payment.provider.repository.PaymentCardRepository;
import proselyte.payment.provider.repository.TransactionRepository;
import proselyte.payment.provider.service.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @InjectMocks
    private TransactionService serviceUnderTest;

    @Test
    @DisplayName("Test create top transaction functionality")
    public void givenCreateTopUpTransactionRequest_whenCreateTopUpTransaction_thenTransactionCreated() throws ExecutionException, InterruptedException {
        //  given
        CreateTransactionRequest request = DataUtils.createTransactionRequest(500);

        MerchantEntity merchantPersisted = DataUtils.getMerchantPersisted();
        PaymentCardEntity paymentCardPersisted = DataUtils.getPaymentCardPersisted();

        BankAccountEntity merchantBankAccount = new BankAccountEntity(1L, CurrencyType.RUB, 10_000, merchantPersisted.getId());
        BankAccountEntity customerBankAccount = new BankAccountEntity(2L, CurrencyType.RUB, 5_000, paymentCardPersisted.getId());

        BDDMockito.given(merchantRepository.findById(anyString()))
                .willReturn(Mono.just(merchantPersisted));
        BDDMockito.given(paymentCardRepository.findByCardNumber(anyString()))
                .willReturn(Mono.just(paymentCardPersisted));
        BDDMockito.given(bankAccountRepository.findAllByOwnerUid(merchantPersisted.getId()))
                .willReturn(Flux.just(merchantBankAccount));
        BDDMockito.given(bankAccountRepository.findAllByOwnerUid(paymentCardPersisted.getId()))
                .willReturn(Flux.just(customerBankAccount));
        BDDMockito.given(bankAccountRepository.save(any(BankAccountEntity.class)))
                .willReturn(Mono.just(customerBankAccount.toBuilder()
                        .balance(customerBankAccount.getBalance() - request.amount())
                        .build()));
        BDDMockito.given(transactionRepository.save(any(TransactionEntity.class)))
                .willReturn(Mono.just(TransactionEntity.builder()
                        .transactionStatus(TransactionStatus.IN_PROGRESS)
                        .transactionType(TransactionType.TOP_UP)
                        .fromBankAccountId(customerBankAccount.getId())
                        .toBankAccountId(merchantBankAccount.getId())
                        .amount(request.amount())
                        .notificationUrl(request.notificationUrl())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .language(request.language())
                        .build()));
        //  when
        TransactionEntity transaction = serviceUnderTest.createTopUpTransaction(request).block();
        //  then

        assertThat(transaction).isNotNull();
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));

    }

    @Test
    @DisplayName("Test try create top up transaction with amount ore then customer bank account balance")
    public void givenCreateTopUpTransactionRequestWithAmountMoreThenCustomerBankAccountBalance_whenCreateTopUpTransaction_thenExceptionOccurred() {
        //  given
        CreateTransactionRequest request = DataUtils.createTransactionRequest(5500);

        MerchantEntity merchantPersisted = DataUtils.getMerchantPersisted();
        PaymentCardEntity paymentCardPersisted = DataUtils.getPaymentCardPersisted();

        BankAccountEntity merchantBankAccount = new BankAccountEntity(1L, CurrencyType.RUB, 10_000, merchantPersisted.getId());
        BankAccountEntity customerBankAccount = new BankAccountEntity(2L, CurrencyType.RUB, 5_000, paymentCardPersisted.getId());

        BDDMockito.given(merchantRepository.findById(anyString()))
                .willReturn(Mono.just(merchantPersisted));
        BDDMockito.given(paymentCardRepository.findByCardNumber(anyString()))
                .willReturn(Mono.just(paymentCardPersisted));
        BDDMockito.given(bankAccountRepository.findAllByOwnerUid(merchantPersisted.getId()))
                .willReturn(Flux.just(merchantBankAccount));
        BDDMockito.given(bankAccountRepository.findAllByOwnerUid(paymentCardPersisted.getId()))
                .willReturn(Flux.just(customerBankAccount));

        //  when
        assertThrows(ApiException.class, () -> serviceUnderTest.createTopUpTransaction(request).block());
        //  then

        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    @DisplayName("Test get transaction by id functionality")
    public void givenTransactionId_whenGetTransactionById_thenTransactionReturned() {
        //given
        String transactionId = UUID.randomUUID().toString();
        BDDMockito.given(transactionRepository.findById(anyString()))
                .willReturn(Mono.just(TransactionEntity.builder()
                        .id(transactionId)
                        .build()));
        //when
        TransactionEntity transactionById = serviceUnderTest.getTransactionById(transactionId).block();
        //then
        assertThat(transactionById).isNotNull();
        assertThat(transactionById.getId()).isEqualTo(transactionId);
    }

    @Test
    @DisplayName("Test get transaction by wrong id functionality")
    public void givenWrongTransactionId_whenGetTransactionById_thenNotFoundExceptionThrown() {
        //given
        String transactionId = UUID.randomUUID().toString();
        BDDMockito.given(transactionRepository.findById(anyString()))
                .willReturn(Mono.empty());
        //when
        assertThrows(NotFoundException.class, () -> serviceUnderTest.getTransactionById(transactionId).block());
        //then

    }
}
