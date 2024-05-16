package proselyte.payment.provider.exception;

public class CurrenciesDoesNotMatchException extends ApiException {
    public CurrenciesDoesNotMatchException(String message) {
        super("CURRENCIES_DOES_NOT_MATCH", message);
    }
}
