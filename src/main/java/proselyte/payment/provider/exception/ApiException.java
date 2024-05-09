package proselyte.payment.provider.exception;

public class ApiException extends RuntimeException {

    protected String errorCode;

    public ApiException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
