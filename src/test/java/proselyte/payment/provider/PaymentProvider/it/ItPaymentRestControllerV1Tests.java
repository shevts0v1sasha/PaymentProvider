package proselyte.payment.provider.PaymentProvider.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import proselyte.payment.provider.PaymentProvider.config.PostgreTestcontainerConfig;
import proselyte.payment.provider.PaymentProvider.utils.DataUtils;
import proselyte.payment.provider.dto.CreateTransactionRequest;
import proselyte.payment.provider.dto.CreateTransactionResponse;
import proselyte.payment.provider.repository.TransactionRepository;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgreTestcontainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ItPaymentRestControllerV1Tests {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Test create top up transaction functionality")
    public void givenCreateTopUpTransactionRequest_whenCreateTopUpTransaction_thenTransactionCreated() {
        //given
        CreateTransactionRequest transactionRequest = DataUtils.createTransactionRequest(500);
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("/api/v1/payments/topups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), CreateTransactionResponse.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.transaction_id").isNotEmpty()
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");

    }

    @Test
    @DisplayName("Test create payout transaction functionality")
    public void givenCreatePayoutTransactionRequest_whenCreatePayoutTransaction_thenTransactionCreated() {
        //given
        CreateTransactionRequest transactionRequest = DataUtils.createTransactionRequest(500);
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("/api/v1/payments/payouts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), CreateTransactionResponse.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.transaction_id").isNotEmpty()
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");
    }

    @Test
    @DisplayName("Test get transaction by id functionality")
    public void givenTransactionId_whenGetTransactionById_thenTransactionReturned() {
        //given
        CreateTransactionRequest transactionRequest = DataUtils.createTransactionRequest(500);
        WebTestClient.ResponseSpec createResult = webTestClient.post()
                .uri("/api/v1/payments/topups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), CreateTransactionResponse.class)
                .exchange();
        String transactionId = createResult.returnResult(CreateTransactionResponse.class).getResponseBody().blockFirst().transactionId();
        System.out.println("Created id: " + transactionId);

        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri("/api/v1/payments/transaction/%s/details".formatted(transactionId))
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(transactionId)
                .jsonPath("$.amount").isEqualTo(transactionRequest.amount())
                .jsonPath("$.language").isEqualTo(transactionRequest.language());
    }

    @Test
    @DisplayName("Test get transaction by wrong id functionality")
    public void givenWrongTransactionId_whenGetTransactionById_thenExceptionReturned() {
        //given
        String transactionId = "WRONG_ID";
        //when
        WebTestClient.ResponseSpec result = webTestClient.get()
                .uri("/api/v1/payments/transaction/%s/details".formatted(transactionId))
                .exchange();
        //then
        result.expectStatus().isNotFound();
    }
}
