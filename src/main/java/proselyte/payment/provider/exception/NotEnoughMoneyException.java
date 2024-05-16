package proselyte.payment.provider.exception;

public class NotEnoughMoneyException extends ApiException {

    public NotEnoughMoneyException(String message) {
        super("NOT_ENOUGH_MONEY_EXCEPTION", message);
    }
}
