package proselyte.payment.provider.exception;

public class WebhookNotReceivedException extends RuntimeException {

    public WebhookNotReceivedException(String message) {
        super(message);
    }
}
