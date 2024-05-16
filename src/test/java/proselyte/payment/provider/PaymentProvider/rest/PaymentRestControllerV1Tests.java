package proselyte.payment.provider.PaymentProvider.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import proselyte.payment.provider.PaymentProvider.utils.DataUtils;
import proselyte.payment.provider.dto.CreateTransactionRequest;
import proselyte.payment.provider.dto.CreateTransactionResponse;
import proselyte.payment.provider.entity.TransactionEntity;
import proselyte.payment.provider.entity.TransactionStatus;
import proselyte.payment.provider.rest.PaymentRestControllerV1;
import proselyte.payment.provider.service.TransactionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ComponentScan({"proselyte.payment.provider.errorhandling"})
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {PaymentRestControllerV1.class})
public class PaymentRestControllerV1Tests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransactionService transactionService;

//    @Test
    @DisplayName("Test create top up transaction functionality")
    public void givenCreateTopUpTransactionRequest_whenCreateTopUpTransaction_thenTransactionCreated() {
        //given
        CreateTransactionRequest transactionRequest = DataUtils.createTransactionRequest(500);
        String transactionUid = UUID.randomUUID().toString();
        BDDMockito.given(transactionService.createTopUpTransaction(any(CreateTransactionRequest.class)))
                .willReturn(Mono.just(TransactionEntity.builder()
                        .id(transactionUid)
                        .transactionStatus(TransactionStatus.IN_PROGRESS)
                        .build()));
        //when
        WebTestClient.ResponseSpec result = webTestClient.post()
                .uri("api/v1/payments/topups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), CreateTransactionResponse.class)
                .exchange();
        //then
        result.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(transactionUid)
                .jsonPath("$.message").isEqualTo("OK")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");

    }
}
