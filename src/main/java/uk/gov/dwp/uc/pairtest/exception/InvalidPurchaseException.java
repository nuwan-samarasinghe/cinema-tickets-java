package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {

    /**
     * give a custom runtime exception based on a message
     *
     * @param msg exception message
     */
    public InvalidPurchaseException(String msg) {
        super(msg);
    }

    /**
     * give a custom runtime exception
     *
     * @param msg       exception message
     * @param throwable exception
     */
    public InvalidPurchaseException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
