package co.igorski.exceptions;

public class SnitcherException extends Exception {
    public SnitcherException(String message, Exception e) {
        super(message, e);
    }

    public SnitcherException(String message) {

        super(message);
    }
}
