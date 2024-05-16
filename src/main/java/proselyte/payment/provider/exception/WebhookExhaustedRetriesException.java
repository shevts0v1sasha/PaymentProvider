package proselyte.payment.provider.exception;

public class WebhookExhaustedRetriesException extends RuntimeException {

    public WebhookExhaustedRetriesException(String message) {
        super(message);
    }
}
